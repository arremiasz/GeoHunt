// Run: npm install ws uuid@8.3.2
const { WebSocketServer, WebSocket } = require('ws');
const http = require('http');
const url = require('url');
const { v4: uuidv4 } = require('uuid');

// --- Server State ---

// --- MODIFIED ---
// lobbies: Map<lobbyId, { members: Set<WebSocket>, leaderUsername: string | null }>
// We now store the leader's USERNAME, not their WS object.
const lobbies = new Map();

// clients: Map<WebSocket, { username: string, currentLobbyId: string | null }>
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

  // --- 5. Handle messages ---
  ws.on('message', (rawMessage) => {
    const message = rawMessage.toString().trim();
    const senderInfo = clients.get(ws);
    const currentLobbyId = senderInfo.currentLobbyId;

    // --- CASE 1: User is NOT in a lobby ---
    if (!currentLobbyId) {
      if (message === 'create') {
        const lobbyId = `lobby-${uuidv4().substring(0, 6)}`;
        
        // --- MODIFIED ---
        const newLobby = {
          members: new Set(),
          leaderUsername: senderInfo.username // Set leader by username
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

        const joinMsg = `User ${senderInfo.username} joined the lobby`;
        broadcast(lobby.members, joinMsg, ws);

        lobby.members.add(ws);
        senderInfo.currentLobbyId = lobbyId;
        
        // --- NEW RECONNECT LOGIC ---
        // Check if this user is the "true leader" who is rejoining
        if (senderInfo.username === lobby.leaderUsername) {
            console.log(`Leader ${senderInfo.username} has reconnected to lobby ${lobbyId}`);
            ws.send(`Welcome back, leader. You have reclaimed your role.`);
            broadcast(lobby.members, `Leader ${senderInfo.username} has reconnected.`, ws);
        } else {
            ws.send(`Successfully joined lobby ${lobbyId}`);
        }
      }
      
      else {
        ws.send('Error: You are not in a lobby. (Try "create" or "join {lobbyId}")');
      }
    }
    
    // --- CASE 2: User IS in a lobby ---
    else {
      const lobby = lobbies.get(currentLobbyId);

      // --- NEW COMMAND: leave ---
      if (message === 'leave') {
        handleLeave(ws, senderInfo, lobby, currentLobbyId);
        return; // handleLeave will do the rest
      }

      // --- MODIFIED LEADER CHECK ---
      // We check against the stored username, not the ws object.
      if (senderInfo.username === lobby.leaderUsername) {
        // If sender is leader, broadcast *any* message they send.
        console.log(`Broadcasting leader message from ${senderInfo.username}: "${message}"`);
        broadcast(lobby.members, message);
      } 
      else {
        // Sender is in a lobby, but is NOT the leader
        ws.send('Error: Only the lobby leader can send commands.');
      }
    }
  });

  // --- 6. Handle disconnections (UPDATED: Lobby Persists) ---
  ws.on('close', () => {
    const disconnectedClient = clients.get(ws);
    if (!disconnectedClient) return;

    console.log(`Client disconnected: ${disconnectedClient.username}`);
    
    const lobbyId = disconnectedClient.currentLobbyId;
    if (lobbyId) {
      const lobby = lobbies.get(lobbyId);
      if (lobby) {
        // 1. Remove the client's connection
        lobby.members.delete(ws); 
        
        // 2. Announce their departure
        const disconnectMsg = `User ${disconnectedClient.username} disconnected`;
        broadcast(lobby.members, disconnectMsg);

        // 3. (REMOVED) We no longer check if the lobby is empty.
        // The lobby will persist with 0 members, waiting for a reconnect.
      }
    }
    clients.delete(ws);
  });

  ws.on('error', console.error);
});

// --- 7. (UPDATED) Handler for "leave" command ---
function handleLeave(ws, senderInfo, lobby, lobbyId) {
    console.log(`User ${senderInfo.username} is leaving lobby ${lobbyId}`);
    
    // Remove them from the lobby
    lobby.members.delete(ws);
    senderInfo.currentLobbyId = null;
    ws.send("You have left the lobby."); // Confirm to the user
    
    // Announce their departure
    broadcast(lobby.members, `User ${senderInfo.username} left the lobby`);
    
    // Check if the leader is leaving
    if (senderInfo.username === lobby.leaderUsername) {
        console.log(`Leader ${senderInfo.username} deliberately left lobby ${lobbyId}.`);
        
        // If members are left, promote the next one
        if (lobby.members.size > 0) {
            const newLeader = lobby.members.values().next().value;
            const newLeaderInfo = clients.get(newLeader);
            
            lobby.leaderUsername = newLeaderInfo.username; // Assign new leader by USERNAME
            
            console.log(`New leader for ${lobbyId} is ${newLeaderInfo.username}`);
            broadcast(lobby.members, `User ${newLeaderInfo.username} is now the lobby leader`);
        }
    }
    
    // --- THIS IS THE NEW LOGIC ---
    // After all other logic, check if the lobby is now empty.
    // This block only runs on a deliberate "leave" command.
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

// --- 9. Start the HTTP server ---
httpServer.listen(8080, () => {
  console.log('🚀 Resilient Leader Server (with Heartbeat) listening on ws://localhost:8080');
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