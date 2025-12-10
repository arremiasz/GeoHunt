const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(bodyParser.json());

// Store mocks in memory
// { path: string, method: string, response: any, status: number }
let mocks = [];

// Management API to add/remove mocks
app.post('/_api/mocks', (req, res) => {
  const { path, method, response, status } = req.body;
  if (!path || !method) {
    return res.status(400).json({ error: 'Path and method are required' });
  }
  
  // Remove existing mock for same path/method
  mocks = mocks.filter(m => !(m.path === path && m.method === method));
  
  mocks.push({
    path,
    method: method.toUpperCase(),
    response: response || {},
    status: status || 200
  });
  
  console.log(`Added mock: ${method} ${path}`);
  res.json({ success: true, mocks });
});

app.get('/_api/mocks', (req, res) => {
  res.json(mocks);
});

app.delete('/_api/mocks', (req, res) => {
  const { path, method } = req.body;
  mocks = mocks.filter(m => !(m.path === path && m.method === method));
  res.json({ success: true, mocks });
});

// Middleware to handle mocked routes
app.use((req, res, next) => {
  if (req.path.startsWith('/_api/')) {
    return next();
  }

  const mock = mocks.find(m => m.path === req.path && m.method === req.method);
  if (mock) {
    return res.status(mock.status).json(mock.response);
  }

  next();
});

app.listen(PORT, () => {
  console.log(`Mock server running on http://localhost:${PORT}`);
});
