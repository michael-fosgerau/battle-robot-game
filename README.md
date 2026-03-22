# Battle Robot Game - Project Setup

## Project Architecture

### Backend (Quarkus)
- **Framework**: Quarkus 3.8.1 with RESTEasy Reactive
- **Build System**: Gradle Kotlin DSL
- **Java Version**: 17

### Frontend (HTML/JavaScript)
- **UI Framework**: Vanilla HTML/CSS/JavaScript
- **Static Files Location**: `src/main/resources/META-INF/resources/`

## Directory Structure

```
src/
  main/
    java/net/fosgerau/game/
      api/
        GameResource.java          # REST API endpoints
    resources/
      META-INF/resources/
        index.html                 # Main HTML page
        styles.css                 # Styles
        app.js                     # Frontend application logic
      application.properties       # Quarkus configuration
```

## API Endpoints

### Current Endpoints (Placeholders)
- `GET /api/game/status` - Get current game status (returns empty JSON)
- `POST /api/game/start` - Start a new game (returns empty JSON)

## Running the Server

### Development Mode
```bash
./gradlew quarkusDev
```

The server will start at `http://localhost:8080`

### Production Build
```bash
./gradlew build
```

## Project Status
✅ Quarkus backend configured
✅ Basic REST API scaffold created
✅ HTML/CSS/JavaScript UI framework ready
✅ Server compiles and runs successfully
✅ Static files serving correctly

## Next Steps
1. Add game logic to the backend (game state, robot entities, etc.)
2. Implement game mechanics (scanning, movement, direction, etc.)
3. Build out the UI components (game board, robot display, controls, etc.)
4. Add WebSocket support for real-time game updates if needed

