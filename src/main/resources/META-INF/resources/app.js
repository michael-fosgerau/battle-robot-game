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
        this.robotElement = null;
        this.gridSize = 4;
        this.program = new Array(this.gridSize * this.gridSize).fill(null);
        this.draggedBlock = null;

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
        this.setupDragAndDrop();
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

    setupDragAndDrop() {
        const blockButtons = document.querySelectorAll('.block-button');
        blockButtons.forEach(btn => {
            btn.addEventListener('dragstart', (e) => this.onBlockDragStart(e));
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
                const blockText = this.getBlockText(instruction);
                block.innerHTML = `${blockText}<button class="remove-btn" data-slot="${index}">×</button>`;
                block.addEventListener('dragstart', (e) => this.onBlockDragStart(e));
                block.querySelector('.remove-btn').addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.removeBlockFromSlot(index);
                });
                slot.appendChild(block);
            }
        });
    }

    getBlockText(instruction) {
        const textMap = {
            'moveLeft': '⬅️ Left',
            'moveRight': '➡️ Right',
            'moveUp': '⬆️ Up',
            'moveDown': '⬇️ Down'
        };
        return textMap[instruction] || instruction;
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
        try {
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
            const result = await response.json();
            console.log('Program sent to backend:', result);
        } catch (error) {
            console.error('Error sending program to backend:', error);
        }
    }

    async onStartClick() {
        console.log('Start button clicked');
        this.startButton.disabled = true;
        this.stopButton.disabled = false;

        try {
            const result = await this.startGame();
            console.log('Game started:', result);
            this.gameRunning = true;
            this.renderRobot(result.robot);
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
            this.renderRobot(result.robot);
        } catch (error) {
            console.error('Error stopping game:', error);
        }
    }

    async getStatus() {
        try {
            const response = await fetch(`${this.apiBase}/state`);
            return await response.json();
        } catch (error) {
            console.error('Error fetching game status:', error);
        }
    }

    async startGame() {
        try {
            const response = await fetch(`${this.apiBase}/start`, { method: 'POST' });
            return await response.json();
        } catch (error) {
            console.error('Error starting game:', error);
        }
    }

    async stopGame() {
        try {
            const response = await fetch(`${this.apiBase}/stop`, { method: 'POST' });
            return await response.json();
        } catch (error) {
            console.error('Error stopping game:', error);
        }
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
                this.renderRobot(state.robot);
                // Update program from backend only if instruction pointer changed (for UI feedback)
                // Don't overwrite user's program arrangement
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

    renderRobot(robot) {
        if (!this.robotElement) {
            this.robotElement = document.createElement('div');
            this.robotElement.className = 'robot';
            this.gameBoard.appendChild(this.robotElement);
        }

        // Calculate position on the board
        // Board is 600x600, grid is 30x30, so each cell is 20px
        const cellSize = 20;
        const x = robot.x * cellSize;
        const y = robot.y * cellSize;

        this.robotElement.style.left = x + 'px';
        this.robotElement.style.top = y + 'px';

        console.log(`Robot rendered at (${robot.x}, ${robot.y}) -> pixel (${x}, ${y})`);
    }
}

// Initialize the game client when the page loads
document.addEventListener('DOMContentLoaded', () => {
    window.gameClient = new GameClient();
    console.log('Battle Robot Game - Ready');
});

