Feature: Updating character conversation when resuming
	As a player
	So that I can reinstall the application
	I want to be able to view all of my conversations

	Background:
		Given there is a character named "Otis"
		And I have received a message with text "To be or not to be?"
		And I have chosen "To be." as my response

	Scenario: Player resumes conversation after reinstalling the application
		Given that I have a previous conversation with "Otis"
		And I have just reinstalled the application
		When I select the character "Otis"
		Then I should see a message from "Otis" with text "To be or not to be?"
		And I should see a response with text "To be."
