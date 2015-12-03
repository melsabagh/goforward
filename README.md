# GoForward

This is a secure implementation of the cards game Go Forward (AKA: thirteen). The project implements multiple secure-design principles and provides an assurance cause. The assurance case presented here is not complete and should be taken with a grain of salt. Nevertheless, the project gives an idea of what it takes to securely design and develop an application and support the security claims with an assurance case.

---

# Overview

The server, client and game engine are all implement in Java. We implemented a dedicated, stateful, game server where client connections and sessions are long-lived. The game server handles multiple connections simultaneously where each connection gets its own session handler thread. The game-engine is standalone and signals registered listeners of changes. The system uses JSON for serialization over a secure implementation of a channel encoder/decoder. The system adopts an asynchronous one-to-one communication model. The server protects against clients attacks and cheating. The client is implemented in Java Swing (Nimbus L&F) and has protection against server attacks as well.

---

# Installation Instructions

The following instructions assume a Linux-like environment. For Windows, please refer to the Windows Command Line manual. The project is self-contained and requires no separate installation of the dependencies. A compatible JRE for Java 1.7 is required to build or run the project.

## Starting from the source

To build the source code, need Ant v1.7 or later is required. The ant build file (build.xml) is located at the root of the project source code. To build the project, proceed as follows:

```bash
cd 'directory/where/you/downloaded/goforward-src.zip'
unzip goforward-src.zip -d goforward
cd goforward
ant jar
```

When the build is done, a new directory ‘<span class="SpellE">goforward</span>/<span class="SpellE">dist</span>’ will be created with two subdirectories: ‘client’ and ‘server,’ each of which contains the corresponding binaries and dependencies. To install the binaries, simply copy the ‘client’ and the ‘server’ directories to any directories of your choice.

## Starting from the binaries

To install the binaries, unzip the archives ‘goforward-client-bin.zip’ and ‘goforward-server-bin.zip’) into directories of your choice. Each unzipped archive contains an executable jar along with required dependencies.

## Post-Install

After successful installation, set the execute flag of the starter shell script in the client and the server directories:

```bash
cd 'directory/where/you/installed/the/client'
chmod +x goforward-client.sh
```
```bash
cd 'directory/where/you/installed/the/server'
chmod +x goforward-server.sh
```

The shell scripts start the executable jars as well as set the required JVM parameters to accept the self-signed TLS certificate (a self-signed key-store is located at the root directory of the server and a corresponding trust-store at the root directly of the client). These parameters are not global and will apply to only the JVM instances of the client and the server.

---

# Operating Instructions

To start the server, run:
```bash
cd 'directory/where/you/installed/the/server'
./goforward-server.sh
```
Once the server has finished loading, it will listen on port 4443 for incoming connections.

And for the client(s), run:
```bash
cd 'directory/where/you/installed/the/client'
./goforward-client.sh
```

Client are programmed to by default connect to the server at localhost:4443. Once a client starts, the user must either login or register. After successful authentication, the user can join a game, view leaderboard, view history and audits, etc.

---

# Game rules

Adapted from: http://www.pagat.com/climbing/thirteen.html

The first player to make the first move is randomly selected. Each player in turn must now either beat the previously played card or combination, by playing a card or combination that beats it, or pass and not play any cards.

The play goes around the table as many times as necessary until someone plays a card or combination that no one else beats. When this happens, all the played cards are set aside, and the person whose play was unbeaten starts again by playing any legal card or combination face up to the center of the table.

If you pass you are locked out of the play until someone makes a play that no one beats. Only when the cards are set aside and a new card or combination is led are you entitled to play again.

The legal plays in the game are the following:

The legal plays in the game are the following: 
-	Single card: the lowest single card is the ♠3 and the highest is the ♥2. 
-	Pair: two cards of the same rank, such as ♣7-♦7 or ♦Q-♠Q. 
-	Triple: three cards of the same rank, such as ♦5-♥5-♣5 
-	Four of a kind: four cards of the same rank, such as ♥9-♦9-♣9-♠9. 
-	Sequence: three or more cards of consecutive rank (the suits can be mixed), such as ♦4-♠5-♥6 or ♦J-♥Q-♥K-♣A-♦2. Sequences cannot "turn the corner" between two and three, e.g., A-2-3 is not a valid sequence because 2 is high and 3 is low. 
-	Double Sequence: three or more pairs of consecutive rank, such as 3-3-4-4-5-5 or 6-6-7-7-8-8-9-9. 

In general, a combination can only be beaten by a higher combination of the same type and same number of cards. To decide which of two combinations of the same type is higher you just look at the highest card in the combination. For example ♥7-♠7 beats ♦7-♣7 because the heart beats the diamond. In the same way ♠8-♠9-♦10 beats ♥8-♥9-♣10 because it is the highest cards (the tens) that are compared. 

There are just four exceptions to the rule that a combination can only be beaten by a combination of the same type: 

1. A four of a kind can beat any double sequence and combination of two’s except a four of a kind. A four of a kind can only be beaten by a higher four of a kind.

2. A sequence of three pairs (such as 7-7-8-8-9-9) can beat any single two (but not any other single card). A sequence of three pairs can be beaten by a higher sequence of three pairs. 

3. A sequence of four pairs (such as 5-5-6-6-7-7-8-8) can beat a pair of twos (but not any other pair). A sequence of four pairs can be beaten by a higher sequence of four pairs or a four of a kind. 

4. A sequence of five pairs (such as 8-8-9-9-10-10-J-J-Q-Q) can beat a set of three twos (but not any other three of a kind). A sequence of five pairs can be beaten by a higher sequence of five pairs or a four of a kind. 

Note that these exceptions only apply to beating twos, not other cards. For example, if someone plays an ace you cannot beat it with your four of a kind, but if the ace has been beaten by a two, then your four of a kind can be used to beat the two.  

---

# Dependencies 
All 3rd party libraries used in our project are open-source and freely available. The client depends on the following external libraries:

1. GSON: https://code.google.com/p/google-gson/ 

2. Apache Commons Logging: http://commons.apache.org/proper/commons-logging/ 

And, the server depends on the following:

1. GSON: https://code.google.com/p/google-gson/

2. Apache Commons Logging: http://commons.apache.org/proper/commons-logging/ 

3. Apache Commons Collections: http://commons.apache.org/proper/commons-collections/ 

4. Apache Commons Validator: http://commons.apache.org/proper/commons-validator/ 

5. jBCrypt (by the authors of BCrypt): http://www.mindrot.org/projects/jBCrypt/ 

6. VT-Password: https://code.google.com/p/vt-middleware/wiki/vtpassword/ 

7. jCapatcha: http://jcaptcha.sourceforge.net/ 

8. ORMLite: http://ormlite.com/ 

9. Xerial SQLite JDBC: https://bitbucket.org/xerial/sqlite-jdbc/ 

# Source code structure
The source codebase constitutes of the following directories: 
-	src/client: contains code only accessible to the client.
-	src/server: contains code only accessible to the server.
-	src/shared: contains code common to both the client and the client.

The root package of the project is edu/gmu/isa681. The major packages are: 
-	(client) client/model: actual code of the client.
-	(client) client/controller: main code for manipulating the client model.
-	(server) game: standalone game implementation.
-	(server) server/core: code for the dedicated game server and requests handlers.
-	(server) server/core/db: code for interaction with the backend DB.
-	(shared) ctn: code for asynchronous communication and (de)serialization of requests and responses.
-	(shared) game: beans that strongly-type game data that the server shares with the clients.
In the following sections, we give an overview of the project architecture and some details on the major classes and functionalities.

---

# Main Components
We implemented a dedicated, stateful, game server to handle the clients’ requests. All message exchanges between the client and the server are encoded/decoded by a JSON channel encoder. Clients follow an MVC pattern where the view is implemented in Java SWING (Nimbus L&F). In the following subsections, we give some details on the design of each side of the system. Appendix-A includes UML diagrams for the major parts of the system.

## The Game Server
All named components in this section are under the root src/server/edu/gmu/isa681/.

The server listens for incoming connections and forks child session handlers (threads) to handle each individual connection. The session handlers and their associated connections are long-lived, until the clients log out or a timeout occurs. The following is a list of the main classes on the server side and a brief description of their functionality.

-	server/Server: the main server loop that listens for incoming connections and spawns session handlers as needed.

-	server/SessionHandler: a standalone thread that serves a specific connection (client). This is the only communication interface between the client and the server. The session handler waits for client requests and handles them as needed. For each request type, a private handler method is implemented that takes care of all the work needed to handle the request. The session handler is responsible for maintaining the session state (implemented as a Finite-State Machine) and communicating with the lobby manager. It’s also responsible for communicating the game state to the client whenever it’s changed.

-	server/LobbyManager: a singleton that manages the creation and dissolution of users lobbies and game instances. The lobby manager queues up and serves users as needed. It also maintains the games audit, players stats, and automatically rejoins clients who crashed while in game, unless their corresponding game has timed out. When clients request to join a game, the lobby manager queues them up in the waiting queue. And, whenever enough users are waiting, the lobby manager creates and initializes the required game instances and hands them to the corresponding session handlers (by calling back registered game-ready listeners).

-	game/GoForward: a standalone implementation of GoForward. This is the game engine, and it is completely oblivious to the existence of a server or a game lobby. After a game is instantiated, all it does is take input (player move) and update the game state accordingly. The game state includes the public state (e.g., no of cards in hand) and private state (e.g., actual cards values) of each player as well as the public game state (e.g., current player and his move, next player, timeout, etc.). The GoForward class takes care of validating the player input and accepting or rejecting his proposed move.  Components that are interested in knowing about state changes register listeners with GoForward. For the implementation of the game card as well as the public/private states, please see: src/shared/edu/gmu/isa681/game/.

-	server/Authenticator: generates salted hashes and checks access credentials.

-	server/Validator: contains various general methods for validation of input strings as well as specific validation methods for usernames, emails and password strength.

-	server/db/DBHelper: helper methods for reading/writing the backend DB. All DB interaction passes through here. The specific DB classes (relationally map the DB tables) are listed under the same package. Here, input is validated against the schema of the DB tables by adding annotations on the table fields in the corresponding DB classes, e.g., @DatabaseField(canBeNull=false, unique=true) for the email field in the server/db/UserAccount class.

## Channel Encoder and decoder
All named components in this section are under the root: src/shared/edu/gmu/isa681/.

Communication is done over an encoded channel that encodes classes (must be marked Encodeable) into a (nested) JSON packet with two fields: type and body. The type field stores the fully qualified class name, and the body holds the JSON-encoded contents of the class. Both the client and the server use the same channel implementation. The channel inbound/outbound handler threads are asynchronous, i.e., reads and writes can happen concurrently. This gives us the flexibility to implement any communication model we desire, rather than following the strict stop-and-wait request-response model. For example, the client can send multiple requests without blocking on responses, and the server can send multiple responses without blocking on requests. Delivery order is guaranteed by 1) using synchronous blocking queues inside each handler (inbound or outbound) and 2) assuming the client and the server are running on top of a reliable, ordered-delivery, transmission protocol. For more details on the implementation of the channel, please see: ctn/EncodedChannel. For a list of all the requests and responses, please see the packages: ctn/requests/ and ctn/responses/.

## The Game Client
All named components in this section are under the root src/client/edu/gmu/isa681/.

The client follows a Model-View-Controller pattern. The model implementation is at client/model/ClientImpl. All is does is provide an interface for sending requests over the channel, as well as listen for responses and notify interested registered listeners. For more information on the implementation of the client, please see the packages client/model/, client/view/ and client/controller/.

---

# Assurance Case	
Our system is acceptably secure and meets the security specifications outlined in the project requirements:
###	1. The game is acceptably secure against CWE top 25 vulnerabilities.

1. SQL injection. All DB interaction on the server side is done through ORMLite, which internally uses only prepared statements (according to ORMLite design docs). As a double check, we set the log level to DEBUG and log all the statements that execute over the DB (without the parameters, since they may contain sensitive information, e.g., passwords) and checked that they are indeed prepared statements.
    
2. OS command injection. No component of the system invokes external OS commands. The server and the client do not read or write any files based on remote input.
    
3. Buffer overflow. Java automatically checks array bounds, and we have not implemented any native code in the system.
    
4. Cross-site scripting. The game server is statful and the client single TLS connection and session are long-lived. For an attacker to inject any content in a session, he needs to subvert that very specific TLS connection. Additionally, all requests and responses are strongly-typed, and the client renders all strings received from the server (e.g., player names and scores) as text.

5. Missing authentication. Clients authenticate the server by its TLS certificate. The server authenticates each client using a username and a password. To register, clients are required to choose a strong password (validated using VT-Password library). Requests from clients are only accepted after they successfully log in. The session handler strictly implements the following FSM. Transitions that are not shown are automatically rejected and will not change the state of the session.

    |Current State	|Event	|New State|
    |:-------------:|:-----:|:-------:|
    |-	|-	|Not logged in|
    |Not logged in	|Register	|Not logged in|
    |Not logged in	|Login	|Idle|
    |Not logged in	|Logout	|Not logged in|
    |Idle	|Join game	|Waiting|
    |Idle	|View stats/audit	|Idle|
    |Idle	|Logout	|Not logged in|
    |Waiting	|Game ready	|In game|
    |Waiting	|View stats/audit	|Waiting|
    |Waiting	|Logout	|Not logged in|
    |In game	|View stats/audit	|In game|
    |In game	|Move	|In game|
    |In game	|Game over	|Idle|
    |In game	|Logout	|Not logged in|

    Clients cannot hijack sessions that do not belong to them, because: 1) once a client logs in, his stateful connection remains alive till he logs or times out. And, 2) the connection is secured by TLS. As an additional layer of security, the username of a session is only set once upon successful login. Any other attempt to set the username will fail. This technically prevents reusing sessions due to programmer mistakes.

6. Missing authorization. The system currently has no administrative area on the server. Clients requests are only limited to register, login, join a game, view audit or logout. Any unsupported requests are ignored. Additionally, clients have no mechanism to request the current game state of any specific game. Instead, the session handler decides when to send out the game state, without interaction from the client side. Also, the unique game and session identifiers are never shared with the clients. Clients have access to the games audit (public by the requirements). For an active game, only the players in that game can see its audit, while other clients will be able to see the audit after the game is over. The session handler on the server strictly enforces that.

7. Use of hard-coded credentials. The system is free from any hard-coded credentials. On the server side, the operator supplies the TLS key-store username and password at runtime.

8. Missing encryption of sensitive data. No data is ever communicated in plain. The communication between the server and the clients is secured with TLS. Also, the server only stores salted-hashes (using BCrypt) of the passwords of the clients, but never the actual passwords (see point 1.19 for details).

9. Unrestricted upload of files with dangerous type. [Not applicable. The server does not support uploading files.] 

10. Reliance on untrusted input.
    1. State information. The server does not store any state information on the client side. A client cannot change his session state directly other than through sending a valid request that causes a state transition (see point 1.5). Game state information is only sent out to clients for rendering but never read back from the clients. 

    2. Parameter validation. All input validation is done on the server side. For requests that have parameters, the server checks the parameters against valid patterns and encodings, using a white list whenever applicable. Usernames, emails and passwords are strictly validated (See point 3 for details). Before any data (DB bean) is written to the DB, ORMLite checks the bean structure against the DB schema and checks the bean data against the guards (defined by annotations) in the bean. For example, username in the src/server/server/core/db/UserAccount bean is annotated as “id,” which guarantees it to be unique and not null. 
For the client, it renders strings received from the server as only text. All HTML rendering capabilities of the client (Java Swing) are disabled.

    3. (De)Serialization. The channel encoder/decoder only accepts classes that are marked by the Encodeable interface. It does not load the contents of any decoded packet unless it passes all the type checks. The runtime type of a packet body is checked without executing any code (including static code) that belongs to the claimed type. The server and the client also check each decoded request/response type against a white list of accepted requests and responses. The server and the client get separate copies of the Encodeable classes, and deserialization only succeeds if the deserialized content structure and types exactly match the target class (feature of the GSON library). That prevents (malicious) alternations by one end from being accepted by the other.

    4. File input from the OS to the server. The operator of the server supplies the path to the key store file and the key store password. The JVM KeyStore and KeyManagerFactory handle the validation of the key store file and verify the key store password. The server also accesses the backend DB. ORMLite handles the validation of the DB schema, where it checks each table schema against the strongly-typed DB beans. See point 1.17 for access control of the DB file.

    5. File input from the OS to the client. The client does not read any external file.

    6. Libraries. We used only well-tested libraries that existed for years and are under active development and maintenance.  For libraries that handle the passwords (jBCrypt and VT-Password) we compiled them from source. Also, we double-checked for the absence of any leakage of the passwords, either to the logs or to external parties, by manually reading through their entire source code. See point 1.16 for more details on libraries. Protection against a malicious JRE is beyond the scope of the project. 

11. Execution with unnecessary privileges. All system components run using the lowest privileges required (user privileges). The server doesn’t require any special privileges other than listening for incoming connections.

12. Cross-site request forgery.
    1. From other clients to the game server. The server does not grant access to sensitive information for any client, and clients cannot view or change account information. The only side effect that could happen is the client being logged out, although that would require the attacker to modify the client view in the JVM at runtime.

    2. From the game client to other servers. The clients (desktop apps) only use predefined views, and their long-lived connections to the server are secured by TLS. Additionally, clients do not directly communicate, and the server controls all the content clients receive, where any strings received by the clients from the server are rendered as text. 

13. Improper limitation of pathname to a restricted directory. [Not applicable]
14. Download of code without integrity check. [Not applicable]
15. Incorrect authorization. See point 1.6.

16. Inclusion of functionality from untrusted control sphere
    1. Runtime privileges. All system components run with the lowest privileges. Compromising the server or the client shall have little impact on the underlying operating system. 

    2. Libraries. The server is packaged with all the required libraries (except the JRE).  All libraries that handle passwords (VT-Password and jBCrypt) in our system are open source, freely available, and widely deployed and tested. During implementation, we did our best such that other libraries never get a runtime handle of any class object that might have the password stored, or else a malicious library could extract the password by reflection. The password also never appears in the log and is omitted from the toString() output of the LoginRequest class.

    3. Reflection. The system does not use reflection except to load the JDBC engine (using a hard-coded class name for the JDBC) and to decode incoming packets. The channel decoder takes careful steps to type check each packet before it is decoded or loaded (see point 1.10.3 for details).

17. Incorrect permission assignment for critical resource. The system currently does not use any configuration files. The DB file is readable/writeable with normal permissions, and we assume it’s the responsibility of the operator to restrict access permissions if needed, e.g., by assigning the server and the DB to a specific user group and assigning file permissions as needed.

18. Use of potentially dangerous function. Our system does not use any dangerous function. Static analysis results using FindBugs and VisualCodeGrepper did not report any occurrences of dangerous calls.

19 Use of broken or risky cryptographic algorithms. The server uses BCrypt, which is the state-of-the-art for generating salted password hashes and well tested. The server TLS private key has 2048 bits, the key algorithm is RSA and the signature algorithm is SHA-256 with RSA, which are all current. We assume it’s the responsibility of the operator to protect the server key-store.

20. Incorrect calculation of buffer size. Static analysis using FindBugs and VisualCodeGrepper shows no incorrect calculations of buffer sizes. Additionally, Java uses dynamic collections and takes care of memory management.

21 Improper restriction of excessive authentication attempts. The design of the systems takes into account rate-limiting excessive requests by using a CAPTCHA. However, since the system does not support resetting passwords, we have not enabled any side effects to excessive login attempts. Also, the system detects and prevents multiple logins for a single user account. For registration, users must correctly solve a CAPTCHA before their request be processed. When invalid credentials are supplied, the server terminates the connection and the session. 

22. URL redirection to untrusted site. [Not applicable]
23. Uncontrolled format string. The system does not use format strings.

24. Integer overflow or wraparound. All operations on integers or other primitive types that may overflow are guarded by bound checks. For the players, final score (the winning percentage) is between 0 and 1 and is calculated as 1/(1 +losses/wins)  rather than wins/(wins+losses) to avoid overflows. Also, the number of losses and wins are checked against overflow before incrementing. 

25. Use of a one-way hash without a salt. The system uses jBCrypt to create salted hashes. See point 1.19 for more details.

### 2. The game counters cheating.
1.	Shuffling. The game engine shuffles cards using the Knuth-Fisher’s shuffling algorithm with a secure source of randomness (SecureRandom).
    
2.	Turn validation. The game engine checks if every player is correctly taking turns.

3.	Player hand validation. The game engine cross-validates the moves received from players against the current cards they have in hand. This detects cheats that may, for example, send duplicate cards or cards that the cheater does not possess.

4.	Move type validation. The game engine checks the type of every offered move against a white list of the possible move types. (Refer to the game rules for the legal move types).

5.	Move value validation. The game engine checks the value of each move against a white list of valid moves. (Refer to the game rules for the calculation of move values).

6.	Stalling. The server and the game engine control all timers. Players cannot gain extra time by crashing and rejoining, since the game engine keeps track of the remaining time of each player, regardless of the connection status.

7.	Level-up cheats. The score function takes both wins and losses into account. This prevents level-up cheats where players alternatively lose or surrender in order to level up quickly, without playing any actual games.

###	3. The server enforces strong limits on usernames and passwords.
1.	Limits on usernames. The server only accepts usernames that satisfy the following.
    -	Unique.
    -	Between 5 and 16 characters in length.
    -	Contain only printable letters and numbers.
    -	Contain at least one letter. 
    
2.	Limits on passwords. The server enforces the following rules on passwords. These are the recommendations of OWASP and Virginia-Tech Middleware Services group (creators of the VT-Password library).
    -	Between 8 and 30 characters in length.
    -	Does not contain the username either in forward or reverse order.
    -	Contains only printable characters.
    -	Contains no whitespaces.
    -	Contains at least three of the following: 1 digit, 1 symbol, 1 uppercase, 1 lowercase.
    -	Contains no alphabetical sequences.
    -	Contains no keyboard sequences.
    -	Contains no numerical sequences.
    -	Contains no more than 4 repeated characters.

The server only accepts (printable) US-ASCII characters for usernames and passwords, because they are guaranteed to be available and render correctly on all platforms. 

###	4. The confidentiality requirement is satisfied.
1.	Confidentiality of communication. All communication is done over TLS, with strong cryptographic parameters (see point 1.19).

2.	Confidentiality of private players states. The server never sends out the private state of players to each other. Each player only receives the game state, the public states (e.g., number of cards in hand) of other players, and the private state (e.g., values of the cards in hand) of himself. 

3.	Confidentiality of audits of active games. The server controls access to the games audit. Only audits of finished games are accessible. For an active game, the audits are only accessible by the players of that specific game. At anytime, users must sign in in order to vide the audits.

4.	Confidentiality of passwords. The server never stores passwords in plain. BCrypt is used to generate salted hashes of passwords (see point 1.19).

###	5. The integrity requirement is satisfied.
1.	Integrity of communication. All communication is secured by TLS, which guarantees message integrity.

2.	Integrity of state information. All client-server connections are long-lived. Each client gets a separate session handler. Each session handler is a standalone thread that shares no information with other session handlers. The state information of each session is never shared with clients. The game state is only sent out to clients when the engine signals a state change, but never read back from the clients.

3.	Integrity of game moves. The game engine strictly validates game moves. The engine validates the turn, hand, type and value of each move before applying it. 

4.	Integrity of audits. The lobby manager modifies the audit only upon acceptance of a valid move (signaled by the game engine). No other component in the system writes to the audit. The initial hand of each player is also recorded in the audits after the game is over, which helps externally validate the trace of moves for each player.

###	6. The availability requirement is (partially) satisfied.
1.	Simultaneous games. The server supports simultaneous games. Each client gets a separate session handler (thread).

2.	Timeouts. The server sets a timeout per each connection. Unless the client is interacting by sending valid requests, his connection will timeout and terminate automatically. The client must login within 1 minute of opening a connection to the server, or the server will terminate the connection. After successful login, the timeout is increased to 5 minutes. While in game, each player gets a timer per turn (90 seconds), and the game is declared over if the current player’s timer expires before he makes a legal move.

3.	Fault tolerance. After successful login, invalid requests are ignored and cause no side effect. In case of a server error, only the session handler where the error occurred in is terminated. If a game instance fails, only the players in that specific game are affected. Players who crash are allowed to join back in and continue the game, unless their timer has expired.

4.	Rate limiting. 
    1.	From authenticated clients. The inbound and outbound handlers in the channel encoder implement a produce/consumer pattern. Rapid requests from any side (be it the server or the client), faster than what the other side can consume, will eventually queue up, block the inbound queue of the recipient, cause future packets to be dropped, and signal to the recipient that the inbound queue is full. Currently, the countermeasure that the recipient takes is to terminate the connection. We left the implementation of a more sophisticated rate limiting scheme (e.g., ) for future work. 

    2.	From un-authenticated clients. Excessive login and registration requests are partially countered (see point 1.21).


### 7. Static analysis results. 
We ran FindBugs and VisualCodeGrepper with the lowest level of confidence over the entire code base. FindBugs did not report any security flaws, and we applied all the applicable style and performance suggestions reported by it.  VisualCodeGrepper found one occurrence of using Random rather than SecureRandom, a class member that was marked public in one of the classes, and some class members that were supposed to be read-only (accessible only through a getter) but were still modifiable by other classes (because they were mutable). It also reported some classes that were not marked as final and had no subclasses. We fixed all the above issues as well as all other applicable style and performance issues reported by VisualCodeGrepper.

