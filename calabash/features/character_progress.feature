Feature: Viewing character conversation progress
	As a player
	So that I know how I am doing
	I want to be able to view my progress

	Background:
		Given there is a character named "Otis"
		And I have played through the first scene with "Otis" with information "Scene 1"
		And I have played through the second scene with "Otis" with information "Scene 2"

	Scenario: Player views character conversation progress
		When I select the character progress for "Otis"
		Then I should see a card with text "Scene 1" in the progress field
		And I should see a card with text "Scene 2" in the progress field
