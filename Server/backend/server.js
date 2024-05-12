const express = require("express");
const app = express();
const bodyParser = require("body-parser");
let serverSocket;
const PORT = 8050;
const server = app.listen(PORT, () => {
  console.log(`Connected to server with port ${PORT}`);
});

// Parse URL-encoded bodies (as sent by HTML forms)
app.use(bodyParser.urlencoded({ extended: true }));

// Parse JSON bodies (as sent by API clients)
app.use(bodyParser.json());

const io = require("socket.io")(server);
app.get("/", (req, res) => {
  const query = req?.body?.query;
  console.log(query);
  serverSocket.emit("searchQuery", query);
  serverSocket.once("response", (args) => {
    res.status(500).json({ msg: args });
  });
});

io.on("connection", (socket) => {
  serverSocket = socket;
  console.log("A user connected");
  socket.emit("createdSocketClientServer", socket.id);
  socket.on("message", (args) => {
    console.log(args);
  });
});
