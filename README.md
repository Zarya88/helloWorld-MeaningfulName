I devided the code into 4 depositories: controllers, dto, models and "repo".

- Under "repo" you can find the main manager for the poll. Here lies all out ugly truths, most of our backend data, our most useful functions and probably a forgotten ";" somewhere.
- Under "model" you will find User, Poll, Vote and VoteOptions - and yes you guessed it, they are responsible for the model of different objects we are using.
- Under "controllers" you will find the controllers, responsible for all your RESTful web services.
- And "dto" is there to look pretty. No, literally, esier to see what data we are working with this way.

WHAT WORKS CURRENTLY:
- Create a new user
- List all users (-> shows the newly created user)
- Create another user
- List all users again (-> shows two users)
- User 1 creates a new poll
- List polls (-> shows the new poll)
- User 2 votes on the poll
- User 2 changes his vote
- List votes (-> empty)
- Delete the one poll

WHAT OUR DEVELOPERS (me) ARE STILL WORKING ON:
- List votes (-> shows the most recent vote for User 2) : currently showing all votes that has been submited
- auto testing: currently only able to test 1) if new user is creatable and 2) method "showUsers"
