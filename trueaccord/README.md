
# How To Run
To run this project, all you need to do is the following:
1. Clone the project to your local directory
2. run the ./runme.sh script (make sure you make it executable first) 

To run the tests, do the following
./gradlew clean build test (linux)

NOTE: I did this on a Mac, so if you have a Linux-based machine, that script will work. If you need to run it on 
a windows machine, I did include a runme_win.bat. (FULL DISCLOSURE - I didn't test the script as I was on a Mac at the time!)

# Design Overview
When approaching this, I wanted to keep it simple, but separate out the code into several sections. I find that this is
is beneficial when setting up for later enhancements. It also makes the code more readable. While it may seem a little bit of overkill to 
have the extra data transitions, it will scale better with this separation of concerns.

From a high level design perspective, data is translated as follows:

rest call -> 
        Json input objects(Debt, PaymentPlan, Payment) -> 
        Entity Objects (DebtEntity->PaymentPlanEntity->PaymentEntity) ->
        DebtOutput

Processing is as follows:

_DebtEntityLoader_ - Is responsible for:
    1. Retrieving the json information from the provided endpoints
    2. Deserializing the results in to "container" elements that were pure json.
    3. Translating Json Objects into Normalized entity objects (this is the part that may be a bit of overkill, but it made things
   much cleaner to have the data normalized so that there was a proper entity relationship maintained with the entities)

_DebtOutputCalculator_ - Is responsible for:
1. Using DebtEntity to calculate each required field
2. Creating DebtOutput Json objects with proper calculated values

Finally, the main application outputs the results. Once again I used the ObjectMapper which outputs in Json Lines format by default. I set up the 
   date format on the serializer itself, and also created a simple currency serializer for the currency fields.
   
   
From a style strategy, I tended to use a more functional programming approach when at all possible. I find that it reduces
side effects, and once comfortable with it, is a very expressive way to code.

Finally, I tried to use as few libraries as possible. I really like the jackson json libraries, so that was a must. Other than
that, I only added in the apache commons collections for a couple of utility functions.


## Testing 
Given the limited time, I only added unit tests for the "complex" code (that being the calculator). 


#Assumptions
- For fractional pennies, I used a round half up approach and always rounded to the penny. This wasn't explicitly called out,
  but it seemed reasonable.
- One huge assumption that I made was that the data would be good coming from the web services. Inspection proved that to be 
  true, but I would normally be much more defensive about this.

#Given more time...
Given more time, and having had the benefit of completing the exercise, here's what I would do if I had more time, or
if I was going to re-think my original implementation:
- Additional unit tests. This was the bare minimum for the complicated logic
- Additional validation in the code. Current code assumes good data. Added a few TODOs for this
- See other TODOs in the code.

N.B. The second commit was a major re-work from the first. I wasn't happy with the first version because there was too
much of a mix between the calculator and the data. Because of this, I created the intermediate entities and then had the 
calculator work on the passed in data, instead of incorporating the data with the calculator. I thought this was much cleaner
and easier to understand.
