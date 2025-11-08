# ernx-consumer-automation

Automation testing project for ernx-consumer application [ https://ernx-consumer.vercel.app ] using Selenium WebDriver and TestNG.

## Test Scenarios

This automation project covers three comprehensive test scenarios that validate the core functionality of the ernx-consumer application. The tests include new user creation, profile management, email OTP handling. Each test scenario is designed to verify critical user flows and ensure the application works as expected.

1. **testRegisterAndCompleteProfile**: Tests the complete user registration and profile setup flow including OTP-based email verification, form validation for first name and last name fields, child profile creation with nickname and gender selection, and verification of successful redirect to the game page after profile completion.

2. **testConfirmActivities**: Validates the activity confirmation functionality by scrolling to practice activities, confirming three practice sessions, and asserting that the progress score correctly updates to "6/100" after all activities are completed.

3. **testLogOut**: Verifies the logout functionality by navigating to settings, clicking the logout button, confirming the logout action, and asserting that the user is successfully redirected back to the login page.

## How to Run

### Prerequisites
- Java 11 or higher
- Maven
- Chrome browser (latest version)

### Execution Steps

1. **Clone the repository**

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run all tests**
   ```bash
   mvn test
   ```

4. **Run tests using TestNG XML**
   - Open the project in your IDE
   - Right-click on `src/test/resources/testng.xml`
   - Select "Run" or "Run as TestNG Suite"

## OTP API

This project uses **Mail.tm API** for temporary email generation and OTP fetching.

- **API Endpoint**: `https://api.mail.tm`
- **Features**:
  - Automatically generates temporary email addresses
  - Fetches OTP codes from received emails
  - No email account setup required
  - Free API service

The `EmailAPI` class handles:
- Email account creation via Mail.tm API
- Authentication token management
- OTP extraction from email messages using regex pattern matching (4-digit codes)

## Source Files

- **`ErnxAutomationTest.java`**: Main test class containing all test scenarios. Includes setup/teardown methods for WebDriver initialization and three test methods:
  - `testRegisterAndCompleteProfile()`: Tests complete registration flow with OTP
  - `testConfirmActivities()`: Tests activity confirmation functionality
  - `testLogOut()`: Tests logout functionality

- **`EmailAPI.java`**: Handles temporary email account creation and OTP retrieval using Mail.tm API. Uses OkHttp3 for REST API calls and Gson for JSON parsing. Automatically creates email accounts, authenticates, and extracts 4-digit OTP codes from received emails.
