// Run: npm install ws uuid@8.3.2
const { WebSocketServer, WebSocket } = require('ws');
const { v4: uuidv4 } = require('uuid');

const wss = new WebSocketServer({ port: 8080 });

// --- Our Central Lobby State ---
const lobbyState = {
  lobbyId: `lobby-${uuidv4().substring(0, 8)}`,
  leaderId: null, 
  players: [],
  settings: {
    radius: 1.0,
    center: { lat: 40.7128, lng: -74.0060 },
    powerups: true
  },
  gameState: 'lobby'
};

// Map to store WebSocket objects <-> Player objects
// The Player object will now be added *after* they join, not on connection.
const clients = new Map();

console.log(`🚀 Advanced Mock Lobby Server started at ws://localhost:8080`);
console.log(`Lobby ID: ${lobbyState.lobbyId}`);

// --- Helper Functions ---

function broadcast(message, excludeClient) {
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN && client !== excludeClient) {
      client.send(message);
    }
  });
}

function sendError(ws, errorMessage) {
  const errorMsg = JSON.stringify({
    type: 'ERROR',
    message: errorMessage
  });
  ws.send(errorMsg);
}

/**
 * Gathers the current lobbyState and broadcasts a LOBBY_UPDATE to everyone.
 */
function broadcastLobbyUpdate() {
  // Create a "serializable" version of the players list for the JSON payload
  const serializablePlayers = lobbyState.players.map(p => ({
    userId: p.id,       // This now comes from the client
    username: p.username, // This now comes from the client
    isReady: p.isReady
  }));

  const updateMessage = JSON.stringify({
    type: 'LOBBY_UPDATE',
    lobbyId: lobbyState.lobbyId,
    leaderId: lobbyState.leaderId,
    players: serializablePlayers,
    settings: lobbyState.settings
  });

  console.log('Broadcasting LOBBY_UPDATE:');
  console.log(JSON.stringify(JSON.parse(updateMessage), null, 2));
  broadcast(updateMessage);
}

// --- Main Connection Logic ---

wss.on('connection', ws => {
  console.log(`🎉 Client connected, awaiting JOIN_LOBBY...`);

  // --- Message Handling for this Client ---
  ws.on('message', rawMessage => {
    let message;
    try {
      message = JSON.parse(rawMessage);
    } catch (e) {
      console.error('Failed to parse JSON:', rawMessage);
      return;
    }

    // Get the player object for the client.
    // Note: This will be undefined if they haven't sent JOIN_LOBBY yet.
    const senderPlayer = clients.get(ws);

    // --- Main Message Router ---
    switch (message.type) {
      
      // THIS IS THE NEW, MOST IMPORTANT CASE
      case 'JOIN_LOBBY': {
        // If client is already joined, ignore.
        if (senderPlayer) break; 

        const { user } = message;
        if (!user || !user.id || !user.displayName) {
          sendError(ws, 'Invalid JOIN_LOBBY message. Must include user object with id and displayName.');
          return;
        }

        // Create the player object from client data
        const newPlayer = {
          id: user.id,
          username: user.displayName,
          isReady: false,
          ws: ws // Keep a reference to their connection
        };

        // Add them to our tracking systems
        clients.set(ws, newPlayer);
        lobbyState.players.push(newPlayer);

        // If they are the first player, make them the leader.
        if (lobbyState.leaderId === null) {
          lobbyState.leaderId = newPlayer.id;
        }

        console.log(`👍 Client ${newPlayer.username} (${newPlayer.id}) has joined the lobby.`);
        
        // Send a full lobby update to EVERYONE.
        // This is how the new client gets the lobbyId and full player list.
        broadcastLobbyUpdate();
        break;
      }
        
      case 'UPDATE_SETTINGS': {
        // Guard Clause: Make sure the client has joined first.
        if (!senderPlayer) {
          sendError(ws, 'You must join the lobby before updating settings.');
          break;
        }

        if (senderPlayer.id !== lobbyState.leaderId) {
          sendError(ws, 'Only the lobby leader can change game settings.');
          break;
        }
        
        lobbyState.settings = { ...lobbyState.settings, ...message.settings };
        broadcastLobbyUpdate();
        break;
      }

      case 'SET_READY': {
        // Guard Clause
        if (!senderPlayer) {
          sendError(ws, 'You must join the lobby before setting ready status.');
          break;
        }

        senderPlayer.isReady = message.isReady;
        broadcastLobbyUpdate();
        break;
      }

      case 'START_GAME': {
        // Guard Clause
        if (!senderPlayer) {
          sendError(ws, 'You must join the lobby before starting the game.');
          break;
        }

        if (senderPlayer.id !== lobbyState.leaderId) {
          sendError(ws, 'Only the lobby leader can start the game.');
          break;
        }

        const allReady = lobbyState.players.every(p => p.isReady);
        if (!allReady) {
          sendError(ws, 'Not all players are ready.');
          break;
        }
        
        lobbyState.gameState = 'in-game';
        
        const gameStartMessage = JSON.stringify({
          type: 'GAME_STARTING',
          challenge: {
            id: 12345,
            streetviewurl: "https://maps.googleapis.com/maps/api/streetview?size=600x400&location=42.0266,-93.6465&key=AIzaSyA4cGMdtzfM4Ub-1agmFLqKP5WLWLLwLLg"
          }
        });
        
        broadcast(gameStartMessage);
        break;
      }
        
      default:
        console.warn(`Unknown message type received: ${message.type}`);
    }
  });

  // --- Disconnection Handling ---
  ws.on('close', () => {
    // Get the player object (if they ever successfully joined)
    const disconnectedPlayer = clients.get(ws);
    
    // Always remove them from the clients map
    clients.delete(ws);

    // If they were a joined player, update the lobby
    if (disconnectedPlayer) {
      console.log(`Client ${disconnectedPlayer.username} disconnected.`);
      
      // Remove from our state
      lobbyState.players = lobbyState.players.filter(p => p.id !== disconnectedPlayer.id);

      // Check if the leader left
      if (disconnectedPlayer.id === lobbyState.leaderId) {
        lobbyState.leaderId = lobbyState.players.length > 0 ? lobbyState.players[0].id : null;
        console.log(`Leader disconnected. New leader is ${lobbyState.leaderId}`);
      }

      // Broadcast the updated lobby to all remaining players
      broadcastLobbyUpdate();
    } else {
      console.log('A client disconnected before joining.');
    }
  });

  ws.on('error', console.error);
});