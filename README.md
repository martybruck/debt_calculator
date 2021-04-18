
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
is beneficial when setting up for later enhancements. It also makes the code more readable. 

From a style strategy, I tended to use a more functional programming approch when at all possible. I find that it reduces
side effects, and once comfortable with it, is a very expressive way to code.

Finally, I tried to use as few libraries as possible. I really like the jackson json libraries, so that was a must. Other than
that, I only added in the apache commons collections for a couple of utility functions.

## Strategy

The starting point for all of this is the DebtCalculatorApplication. I like to keep this class simple as a rule, and just
have it "tell the story" of what the application is going to do.

The overall strategy that I took was to 
1. Load the data into a structure that was easier to work with. This was done in the DebtCalculatorLoader. This involved:
    1. retrieving the json information from the provided endpoints
    2. deserializing the results in to "container" elements that were pure json.
    3. creating indexes on the debt and payment plans by id so that I could access them O(1) to constuct the final
       structure. 
    4. creating the composite calculator structure for each debt that could then be used to calculate the required information.
2. Create calculator classes (one for the debt - DebtOutputCalculator), and one for the PaymentPlan (PaymentPlanOutputCalculator).
   These classes were used for two purposes:
   1. First, they gave a way to provide a normalized view of the data. The Debt (optionally) contained the PaymentPlan,
     and the PaymentPlan contained the list of Payments. This made it much easier to do the calculations (see note below
     on combining Json and Calculators)
   2. They used this normalized structure to calculate the required information. Most of the calculation was done at the
       payment plan level. the debt calculator was used mainly to provide default behavior if there was no payment plan.
3. Output the results. Once again I used the ObjectMapper which outputs in Json Lines format by default. I set up the 
   date format on the serializer itself, and also created a simple currency serializer for the currency fields.

## Testing 
Given the limited time, I only added unit tests for the "complex" code (that being the two calculators). 


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
- I think the overlap between the Json input objects and the calculator is a bit confusing. If I had it all to do
  over again, I would probably separate that out a bit more. I just feels a bit messy to be combining the calculators and
  json objects.
