// Run: npm install ws uuid@8.3.2
const { WebSocketServer, WebSocket } = require('ws');
const http = require('http');
const url = require('url');
const { v4: uuidv4 } = require('uuid');

// --- Server State ---
// --- MODIFIED ---
// lobbies: Map<lobbyId, { 
//   members: Set<WebSocket>, 
//   leaderUsername: string | null,
//   gameState: 'lobby' | 'in-game' // <-- NEW STATE
// }>
const lobbies = new Map();
const clients = new Map();

// --- 1. Create HTTP server ---
const httpServer = http.createServer((req, res) => {
  res.writeHead(404, { 'Content-Type': 'text/plain' });
  res.end('Not found');
});

// --- 2. Create WebSocket server ---
const wss = new WebSocketServer({ noServer: true, clientTracking: true });

// --- 3. Handle 'upgrade' ---
httpServer.on('upgrade', (request, socket, head) => {
  const { pathname } = url.parse(request.url);
  const match = pathname.match(/^\/multiplayer\/([a-zA-Z0-9_-]+)$/);
  if (!match) {
    socket.destroy();
    return;
  }
  const username = match[1];
  wss.handleUpgrade(request, socket, head, (ws) => {
    wss.emit('connection', ws, username);
  });
});

// --- 4. Handle connections ---
wss.on('connection', (ws, username) => {
  console.log(`🎉 Client connected: ${username}`);
  
  ws.isAlive = true;
  ws.on('pong', () => { ws.isAlive = true; });

  clients.set(ws, {
    username: username,
    currentLobbyId: null
  });

  ws.send('Connection Success');

  // --- 5. Handle messages (NOW STATE-AWARE) ---
  ws.on('message', (rawMessage) => {
    const message = rawMessage.toString().trim();
    const senderInfo = clients.get(ws);
    const currentLobbyId = senderInfo.currentLobbyId;

    // --- CASE 1: User is NOT in a lobby ---
    if (!currentLobbyId) {
      if (message === 'create') {
        const lobbyId = `lobby-${uuidv4().substring(0, 6)}`;
        
        const newLobby = {
          members: new Set(),
          leaderUsername: senderInfo.username,
          gameState: 'lobby',
          results: new Map()
        };
        
        newLobby.members.add(ws);
        lobbies.set(lobbyId, newLobby);
        senderInfo.currentLobbyId = lobbyId;
        
        console.log(`Lobby ${lobbyId} created by ${senderInfo.username}`);
        ws.send(`Successfully created lobby ${lobbyId}`);
      }
      
      else if (message.startsWith('join ')) {
        const lobbyId = message.substring(5);
        const lobby = lobbies.get(lobbyId);

        if (!lobby) {
          ws.send('Error: Lobby not found.');
          return;
        }

        if (lobby.gameState === 'in-game') {
          console.log(`User ${senderInfo.username} joining in-progress game...`);
        }

        const joinMsg = `User ${senderInfo.username} joined the lobby`;
        broadcast(lobby.members, joinMsg, ws);

        lobby.members.add(ws);
        senderInfo.currentLobbyId = lobbyId;
        
        if (senderInfo.username === lobby.leaderUsername) {
            console.log(`Leader ${senderInfo.username} has reconnected to lobby ${lobbyId}`);
            ws.send(`Welcome back, leader. You have reclaimed your role.`);
            broadcast(lobby.members, `Leader ${senderInfo.username} has reconnected.`, ws);
        } else {
            ws.send(`Successfully joined lobby ${lobbyId}`);
        }
        
        if (lobby.gameState === 'in-game' && lobby.results.size > 0) {
            console.log(`Sending current leaderboard to reconnecting user ${senderInfo.username}`);
            sendCurrentLeaderboard(lobby, ws);
        }
      }
      
      else {
        ws.send('Error: You are not in a lobby. (Try "create" or "join {lobbyId}")');
      }
    }
    
    // --- CASE 2: User IS in a lobby ---
    else {
      const lobby = lobbies.get(currentLobbyId);

      if (message === 'leave') {
        handleLeave(ws, senderInfo, lobby, currentLobbyId);
        return;
      }

      // --- Sub-Case 2a: GAME IS IN 'LOBBY' STATE ---
      if (lobby.gameState === 'lobby') {
        if (senderInfo.username === lobby.leaderUsername) {
          
          // --- THIS IS THE MODIFIED BLOCK ---
          if (message.startsWith('start ')) {
            const gameId = message.substring(6).trim(); // "start " is 6 chars

            if (gameId) {
              lobby.gameState = 'in-game';
              lobby.results.clear(); // Clear results for the new round
              
              // Broadcast the *full* command to all clients
              broadcast(lobby.members, message); 
              console.log(`Lobby ${currentLobbyId} has started with game ${gameId}.`);
            } else {
              // Send error *only* to the leader
              ws.send("Error: 'start' command must include an ID. (e.g., 'start 12345')");
            }
          } 
          // --- END OF MODIFIED BLOCK ---
          
          else {
            // This is the "leader broadcasts anything" rule (e.g., 'radius 5.0')
            broadcast(lobby.members, message);
          }
        } else {
          // Non-leader in 'lobby' state
          ws.send('Error: Only the lobby leader can send commands.');
        }
      }
      
      // --- Sub-Case 2b: GAME IS IN 'IN-GAME' STATE ---
      else if (lobby.gameState === 'in-game') {
        
        if (message.startsWith('result ')) {
          const result = message.substring(7);
          lobby.results.set(senderInfo.username, result);
          console.log(`Lobby ${currentLobbyId}: Broadcasting leaderboard update from ${senderInfo.username}`);
          broadcastLeaderboardUpdate(lobby);
        }
        
        else if (senderInfo.username === lobby.leaderUsername) {
          if (message === 'end_game') {
            lobby.gameState = 'lobby';
            broadcast(lobby.members, 'GAME_ENDED. Returning to lobby.');
          } else {
            ws.send('Error: Cannot send that command while in-game. (Try "end_game")');
          }
        }
        
        else {
          ws.send("Error: Game is in progress. (Try 'result {your_result}' or 'leave')");
        }
      }
    }
  });

  // --- 6. Handle disconnections ---
  ws.on('close', () => {
    const disconnectedClient = clients.get(ws);
    if (!disconnectedClient) return;

    console.log(`Client disconnected: ${disconnectedClient.username}`);
    
    const lobbyId = disconnectedClient.currentLobbyId;
    if (lobbyId) {
      const lobby = lobbies.get(lobbyId);
      if (lobby) {
        lobby.members.delete(ws); 
        const disconnectMsg = `User ${disconnectedClient.username} disconnected`;
        broadcast(lobby.members, disconnectMsg);
        // Lobby persists even if empty
      }
    }
    clients.delete(ws);
  });

  ws.on('error', console.error);
});

// --- 7. Handler for "leave" command ---
function handleLeave(ws, senderInfo, lobby, lobbyId) {
    console.log(`User ${senderInfo.username} is leaving lobby ${lobbyId}`);
    
    lobby.members.delete(ws);
    senderInfo.currentLobbyId = null;
    ws.send("You have left the lobby.");
    
    broadcast(lobby.members, `User ${senderInfo.username} left the lobby`);
    
    if (senderInfo.username === lobby.leaderUsername) {
        console.log(`Leader ${senderInfo.username} deliberately left lobby ${lobbyId}.`);
        if (lobby.members.size > 0) {
            const newLeader = lobby.members.values().next().value;
            const newLeaderInfo = clients.get(newLeader);
            lobby.leaderUsername = newLeaderInfo.username;
            console.log(`New leader for ${lobbyId} is ${newLeaderInfo.username}`);
            broadcast(lobby.members, `User ${newLeaderInfo.username} is now the lobby leader`);
        }
    }
    
    if (lobby.members.size === 0) {
        lobbies.delete(lobbyId);
        console.log(`Lobby ${lobbyId} is empty after 'leave' command and has been deleted.`);
    }
}

// --- 8. Broadcast helper ---
function broadcast(membersSet, message, excludeClient) {
  membersSet.forEach(client => {
    if (client !== excludeClient && client.readyState === WebSocket.OPEN) {
      client.send(message);
    }
  });
}

/**
 * Constructs and broadcasts the current leaderboard to all lobby members.
 * @param {object} lobby - The lobby object.
 */
function broadcastLeaderboardUpdate(lobby) {
  // 1. Convert the results Map to a plain object: { "Player1": "12345", ... }
  const board = Object.fromEntries(lobby.results);
  
  // 2. Create the message
  const message = `LEADERBOARD_UPDATE ${JSON.stringify(board)}`;

  // 3. Broadcast it to everyone
  broadcast(lobby.members, message);
}

/**
 * Constructs and sends the current leaderboard to a *single* client.
 * Used for reconnecting.
 * @param {object} lobby - The lobby object.
 * @param {WebSocket} ws - The client to send to.
 */
function sendCurrentLeaderboard(lobby, ws) {
  const board = Object.fromEntries(lobby.results);
  const message = `LEADERBOARD_UPDATE ${JSON.stringify(board)}`;
  
  if (ws.readyState === WebSocket.OPEN) {
    ws.send(message);
  }
}

// --- 9. Start the HTTP server ---
httpServer.listen(8080, () => {
  console.log('🚀 State-Aware Server (with Heartbeat) listening on ws://localhost:8080');
});

// --- 10. HEARTBEAT INTERVAL ---
const interval = setInterval(() => {
  wss.clients.forEach(ws => {
    if (ws.isAlive === false) {
      const clientInfo = clients.get(ws) || { username: 'Unknown' };
      console.log(`💔 Heartbeat failed for ${clientInfo.username}. Terminating connection.`);
      return ws.terminate();
    }
    ws.isAlive = false;
    ws.ping(() => {});
  });
}, 30000);

wss.on('close', () => {
  clearInterval(interval);
});