# ChatRoomServer

## Compile/Run instructions:
In folder with all three .java files:
```bash
$ javac -d ./build *.java
$ cd ./build
$ jar -cvfe ChatRoomService.jar ChatRoomService *
$ java -jar ChatRoomService.jar
```

## Setup:
Connect via port **4338**, or modify the port as you wish.\
Port forward to open server to the Internet.

## Commands:
### !username
Sets the username
> !username <$userName>

### !join
Joins a room, or optionally, creates one and joins it
> !join [new] <$roomName>

### !leave
Leaves the current room
> !leave

### !logout
Leaves the current room, and disconnects you from the server
> !logout

### !list
Lists the current available rooms or users in the current room
> !list [rooms|users]

### !help
Lists all available commands, and provides help for them
> !help [$commandName]