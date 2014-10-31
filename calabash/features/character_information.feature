Feature: Viewing character information
	As a player
	So that I can be more invested in the characters' stories
	I want to be able to learn more about them

	Background:
		Given there is a character named "Otis"
		And "Otis" has a photo
		And "Otis" is aged "42"
		And "Otis" is described by "Wears glasses."

	Scenario: Player views information about a character
		When I select the character information about "Otis"
		Then I should see a photo of "Otis" in the "Photo" field
		And I should see "Otis" in the "Name" field
		And I should see "42" in the "Age" field
		And I should see "Wears glasses." in the "Description" field
