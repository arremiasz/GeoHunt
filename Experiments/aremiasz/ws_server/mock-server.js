// Run: npm install ws uuid@8.3.2
const { WebSocketServer, WebSocket } = require('ws');
const http = require('http');
const url = require('url');
const { v4: uuidv4 } = require('uuid');

// --- Server State ---

// --- MODIFIED ---
// lobbies: Map<lobbyId, { members: Set<WebSocket>, leader: WebSocket }>
// Stores all active lobbies, their members, and who the leader is.
const lobbies = new Map();

// clients: Map<WebSocket, { username: string, currentLobbyId: string | null }>
const clients = new Map();

// --- 1. Create a standard HTTP server ---
const httpServer = http.createServer((req, res) => {
  res.writeHead(404, { 'Content-Type': 'text/plain' });
  res.end('Not found');
});

// --- 2. Create the WebSocket server ---
const wss = new WebSocketServer({ noServer: true });

// --- 3. Handle the 'upgrade' event to parse the URL ---
httpServer.on('upgrade', (request, socket, head) => {
  const { pathname } = url.parse(request.url);
  const match = pathname.match(/^\/multiplayer\/([a-zA-Z0-9_-]+)$/);

  if (!match) {
    socket.write('HTTP/1.1 400 Bad Request\r\n\r\n');
    socket.destroy();
    return;
  }

  const username = match[1];

  wss.handleUpgrade(request, socket, head, (ws) => {
    wss.emit('connection', ws, username);
  });
});

// --- 4. Handle new WebSocket connections ---
wss.on('connection', (ws, username) => {
  console.log(`🎉 Client connected: ${username}`);
  
  clients.set(ws, {
    username: username,
    currentLobbyId: null
  });

  ws.send('Connection Success');

// --- 5. Handle incoming messages (Updated: Leader-Broadcast Logic) ---
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
          leader: ws // Set the sender as the leader
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
        
        console.log(`${senderInfo.username} joined lobby ${lobbyId}`);
        ws.send(`Successfully joined lobby ${lobbyId}`);
      }
      
      else {
        // Not in a lobby, and not a valid command
        ws.send('Error: You are not in a lobby. (Try "create" or "join {lobbyId}")');
      }
    }
    
    // --- CASE 2: User IS in a lobby ---
    else {
      const lobby = lobbies.get(currentLobbyId);

      // Check if the sender is the leader
      if (lobby.leader === ws) {
        // --- This is the new rule ---
        // If sender is leader, broadcast *any* message they send.
        console.log(`Broadcasting leader message from ${senderInfo.username} to lobby ${currentLobbyId}: "${message}"`);
        broadcast(lobby.members, message); // Send to *everyone*
      } 
      
      else {
        // Sender is in a lobby, but is NOT the leader
        ws.send('Error: Only the lobby leader can send commands.');
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
        lobby.members.delete(ws); // Remove from the Set
        
        const disconnectMsg = `User ${disconnectedClient.username} disconnected`;
        broadcast(lobby.members, disconnectMsg);

        // --- MODIFIED: Handle leader disconnection ---
        if (disconnectedClient.ws === lobby.leader) {
          console.log(`Lobby ${lobbyId} leader disconnected.`);
          // If members are left, promote the "next" one
          if (lobby.members.size > 0) {
            // Get the first item from the Set
            const newLeader = lobby.members.values().next().value; 
            lobby.leader = newLeader;
            
            const newLeaderInfo = clients.get(newLeader);
            console.log(`New leader for ${lobbyId} is ${newLeaderInfo.username}`);
            broadcast(lobby.members, `User ${newLeaderInfo.username} is now the lobby leader`);
          }
        }
        
        // Clean up empty lobbies
        if (lobby.members.size === 0) {
          lobbies.delete(lobbyId);
          console.log(`Lobby ${lobbyId} is now empty and has been removed.`);
        }
      }
    }
    
    clients.delete(ws);
  });

  ws.on('error', console.error);
});

/**
 * Helper function to broadcast a message to all clients in a lobby (Set).
 * @param {Set<WebSocket>} membersSet - The Set of WebSocket clients.
 * @param {string} message - The string message to send.
 * @param {WebSocket} [excludeClient] - (Optional) A client to exclude.
 */
function broadcast(membersSet, message, excludeClient) {
  membersSet.forEach(client => {
    if (client !== excludeClient && client.readyState === WebSocket.OPEN) {
      client.send(message);
    }
  });
}

// --- 7. Start the HTTP server ---
httpServer.listen(8080, () => {
  console.log('🚀 String-Based Server (with Leader Logic) listening on ws://localhost:8080');
});