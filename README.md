# ernx-consumer-automation

Automation testing project for ernx-consumer application [ https://ernx-consumer.vercel.app/login ] using Selenium WebDriver and TestNG.

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

## Test Scenarios

1. **testRegisterAndCompleteProfile**
   - Generates a temporary email address
   - Enters email and receives OTP
   - Completes OTP verification
   - Fills profile information (first name, last name)
   - Adds a child profile (nickname, gender selection)
   - Completes profile setup and verifies redirect to game page

2. **testConfirmActivities**
   - Scroll to end of page
   - Confirms three practice activities
   - Verifies progress shows "6/100" score

3. **testLogOut**
   - Clicks Settings
   - Clicks Log Out button
   - Confirms logout
   - Verifies redirect to login page

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
