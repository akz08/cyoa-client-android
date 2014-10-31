Feature: Messaging
	As a player
	In order to interact with a character
	I want to receive messages from them and choose responses to their messages

	Background:
		Given there is a character named "Otis"
		And there is a message with text "To be or not to be?" and choices "To be." and "Not to be." available
		And the choice "To be." leads to a message with text "Great!" and delay of "5" seconds

	Scenario: Player chooses a reponse to a message
		When I receive a message from "Otis" with text "To be or not to be?"
		Then I should see the choices panel
		And I should see "To be." and "Not to be." in the choices panel
		And I should see a button labelled "Send" in the choices panel
		
		When I select the choice with text "To be."
		And I press the "Send" button
		Then I should not see the choices panel
		And I should see a new sent message with text "To be."

	Scenario: Player receives a new message from a character
		Given that I have received a message with text "To be or not to be?" from "Otis"
		And I have sent the choice with text "To be."
		And I am viewing my conversation with "Otis"
		When "5" seconds have passed
		Then I should see a new message from "Otis" with text "Great!"

	Scenario: Player receives a notification of a new message from a character
		Given that I have received a message with text "To be or not to be?" from "Otis"
		And I have sent the choice with text "To be."
		And I am not viewing my conversation with "Otis"
		When "5" seconds have passed
		Then I should receive a notification of a new message with text "Great!"
