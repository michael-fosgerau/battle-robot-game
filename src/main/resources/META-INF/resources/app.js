// Battle Robot Game - Frontend Application

class GameClient {
    constructor() {
        this.apiBase = '/api/game';
        this.gameBoard = document.getElementById('game-board');
        this.startButton = document.getElementById('start-button');
        this.programGrid = document.getElementById('program-grid');
        this.clearProgramBtn = document.getElementById('clear-program');
        this.gameRunning = false;
        this.pollInterval = null;
        this.playerRobotElement = null;
        this.aiRobotElement = null;
        this.gridSize = 4; // 4x4 memory grid
        this.program = new Array(this.gridSize * this.gridSize).fill(null);
        this.draggedBlock = null;

        // Block definitions with creative glyphs and labels
        this.blockTypes = [
            { instruction: 'moveUp', glyph: '⬆️', label: 'Move Up', description: 'Move Up' },
            { instruction: 'moveDown', glyph: '⬇️', label: 'Move Down', description: 'Move Down' },
            { instruction: 'moveLeft', glyph: '⬅️', label: 'Move Left', description: 'Move Left' },
            { instruction: 'moveRight', glyph: '➡️', label: 'Move Right', description: 'Move Right' }
        ];

        this.init();
    }

    init() {
        console.log('Game client initialized');
        this.startButton = document.getElementById('start-button');
        this.stopButton = document.getElementById('stop-button');
        this.startButton.addEventListener('click', () => this.onStartClick());
        this.stopButton.addEventListener('click', () => this.onStopClick());
        this.clearProgramBtn.addEventListener('click', () => this.clearProgram());

        this.initializeProgramGrid();
        this.displayAvailableBlocks();
    }

    initializeProgramGrid() {
        this.programGrid.innerHTML = '';
        for (let i = 0; i < this.gridSize * this.gridSize; i++) {
            const slot = document.createElement('div');
            slot.className = 'grid-slot';
            slot.dataset.slot = i;
            slot.addEventListener('dragover', (e) => this.onDragOver(e));
            slot.addEventListener('drop', (e) => this.onDrop(e));
            slot.addEventListener('dragleave', (e) => this.onDragLeave(e));
            this.programGrid.appendChild(slot);
        }
        this.renderProgram();
    }

    displayAvailableBlocks() {
        const container = document.getElementById('available-blocks');
        container.innerHTML = '';

        // Display all 4 movement blocks
        this.blockTypes.forEach((blockType) => {
            const btn = document.createElement('div');
            btn.className = 'block-button';
            btn.draggable = true;
            btn.dataset.instruction = blockType.instruction;
            btn.setAttribute('data-label', blockType.label);
            btn.innerHTML = blockType.glyph;
            btn.addEventListener('dragstart', (e) => this.onBlockDragStart(e));
            container.appendChild(btn);
        });
    }

    onBlockDragStart(e) {
        this.draggedBlock = {
            instruction: e.target.dataset.instruction,
            text: e.target.textContent
        };
        e.dataTransfer.effectAllowed = 'copy';
        console.log('Dragging block:', this.draggedBlock.instruction);
    }

    onDragOver(e) {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'copy';
        e.target.closest('.grid-slot')?.classList.add('drag-over');
    }

    onDragLeave(e) {
        e.target.closest('.grid-slot')?.classList.remove('drag-over');
    }

    async onDrop(e) {
        e.preventDefault();
        e.target.closest('.grid-slot')?.classList.remove('drag-over');

        if (!this.draggedBlock) return;

        const slot = e.target.closest('.grid-slot');
        const slotIndex = parseInt(slot.dataset.slot);

        this.program[slotIndex] = this.draggedBlock.instruction;
        console.log('Program updated at slot', slotIndex, ':', this.draggedBlock.instruction);

        this.renderProgram();
        await this.sendProgramToBackend();
    }

    renderProgram() {
        const slots = document.querySelectorAll('.grid-slot');
        slots.forEach((slot, index) => {
            slot.innerHTML = '';
            if (this.program[index]) {
                const block = document.createElement('div');
                block.className = 'program-block';
                const instruction = this.program[index];
                const blockType = this.blockTypes.find(b => b.instruction === instruction);

                if (blockType) {
                    block.innerHTML = `<span class="glyph">${blockType.glyph}</span><span class="label">${blockType.label}</span><button class="remove-btn" data-slot="${index}">×</button>`;
                }

                block.addEventListener('dragstart', (e) => this.onBlockDragStart(e));
                block.querySelector('.remove-btn').addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.removeBlockFromSlot(index);
                });
                slot.appendChild(block);
            }
        });
    }

    removeBlockFromSlot(index) {
        this.program[index] = null;
        console.log('Block removed from slot', index);
        this.renderProgram();
        this.sendProgramToBackend();
    }

    clearProgram() {
        this.program = new Array(this.gridSize * this.gridSize).fill(null);
        console.log('Program cleared');
        this.renderProgram();
        this.sendProgramToBackend();
    }

    async sendProgramToBackend() {
        const response = await fetch(`${this.apiBase}/program`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                instructions: this.program,
                currentInstructionIndex: 0
            })
        });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const result = await response.json();
        console.log('Program sent to backend:', result);
        return result;
    }

    async onStartClick() {
        console.log('Start button clicked');
        this.startButton.disabled = true;
        this.stopButton.disabled = false;

        try {
            const result = await this.startGame();
            console.log('Game started:', result);
            this.gameRunning = true;
            this.renderRobots(result.robot, result.opponentRobot);
            this.updateStats(result.robot, result.opponentRobot);
            this.startPolling();
        } catch (error) {
            console.error('Error starting game:', error);
            this.startButton.disabled = false;
            this.stopButton.disabled = true;
        }
    }

    async onStopClick() {
        console.log('Stop button clicked');
        this.stopPolling();
        this.stopButton.disabled = true;
        this.startButton.disabled = false;
        this.gameRunning = false;

        try {
            const result = await this.stopGame();
            console.log('Game stopped:', result);
            this.renderRobots(result.robot, result.opponentRobot);
            this.updateStats(result.robot, result.opponentRobot);
        } catch (error) {
            console.error('Error stopping game:', error);
        }
    }

    async getStatus() {
        const response = await fetch(`${this.apiBase}/state`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return await response.json();
    }

    async startGame() {
        const response = await fetch(`${this.apiBase}/start`, { method: 'POST' });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return await response.json();
    }

    async stopGame() {
        const response = await fetch(`${this.apiBase}/stop`, { method: 'POST' });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return await response.json();
    }

    async updateGame() {
        try {
            const response = await fetch(`${this.apiBase}/update`, { method: 'POST' });
            return await response.json();
        } catch (error) {
            console.error('Error updating game:', error);
            return null;
        }
    }

    startPolling() {
        console.log('Starting game polling...');
        this.pollInterval = setInterval(async () => {
            const state = await this.updateGame();
            if (state) {
                console.log('Game state updated:', state);
                this.renderRobots(state.robot, state.opponentRobot);
                this.updateStats(state.robot, state.opponentRobot);
                console.log('Current instruction index:', state.program.currentInstructionIndex);
            }
        }, 500); // Update every 500ms
    }

    stopPolling() {
        if (this.pollInterval) {
            clearInterval(this.pollInterval);
            this.pollInterval = null;
            console.log('Polling stopped');
        }
    }

    renderRobots(playerRobot, aiRobot) {
        this.renderRobot(playerRobot, true);
        this.renderRobot(aiRobot, false);
    }

    renderRobot(robot, isPlayer) {
        let element = isPlayer ? this.playerRobotElement : this.aiRobotElement;

        if (!element) {
            element = document.createElement('div');
            element.className = isPlayer ? 'robot player-robot' : 'robot ai-robot';
            this.gameBoard.appendChild(element);
            if (isPlayer) {
                this.playerRobotElement = element;
            } else {
                this.aiRobotElement = element;
            }
        }

        // Calculate position on the board
        // Board is 600x600, grid is 30x30, so each cell is 20px
        const cellSize = 20;
        const x = robot.x * cellSize;
        const y = robot.y * cellSize;

        element.style.left = x + 'px';
        element.style.top = y + 'px';

        console.log(`${isPlayer ? 'Player' : 'AI'} robot rendered at (${robot.x}, ${robot.y}) -> pixel (${x}, ${y})`);
    }

    updateStats(playerRobot, aiRobot) {
        this.updateRobotStats('player', playerRobot);
        this.updateRobotStats('ai', aiRobot);
    }

    updateRobotStats(robotType, robot) {
        const statsPanelId = `${robotType}-stats`;
        let statsPanel = document.getElementById(statsPanelId);

        if (!statsPanel) {
            console.error(`Stats panel not found: ${statsPanelId}`);
            return;
        }

        // Update battery
        const batteryBar = statsPanel.querySelector('.battery-bar');
        if (batteryBar) {
            batteryBar.style.width = robot.battery + '%';
            batteryBar.textContent = robot.battery + '%';
        }

        // Update health (structural integrity)
        const healthBar = statsPanel.querySelector('.health-bar');
        if (healthBar) {
            healthBar.style.width = robot.structuralIntegrity + '%';
            healthBar.textContent = robot.structuralIntegrity + '%';
        }

        // Update ammo
        const ammoBar = statsPanel.querySelector('.ammo-bar');
        if (ammoBar) {
            ammoBar.style.width = robot.ammo + '%';
            ammoBar.textContent = robot.ammo + '%';
        }
    }
}

// Initialize the game client when the page loads
document.addEventListener('DOMContentLoaded', () => {
    window.gameClient = new GameClient();
    console.log('Battle Robot Game - Ready');
});



