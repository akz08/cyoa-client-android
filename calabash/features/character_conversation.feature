Feature: Viewing conversations with characters
	As a player
	In order to view my interactions with a character
	I want to be able to check received messages and sent responses

	Background:
		Given there is a character named "Otis"
		And I have received a message with text "To be or not to be?"
		And I have chosen "To be." as my response

	Scenario: Player views their conversation with a character
		When I select the "Otis" character
		Then I should see a message from "Otis" with text "To be or not to be?"
		And I should see a response with text "To be."
