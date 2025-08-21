# Framework Overview  

This framework embraces the **BDD methodology**, allowing for test scenarios to be written in a human-readable format using **Gherkin syntax (Given-When-Then)**.  

These scenarios are then linked to the underlying automation code through **Step Definitions**.  

The **Page Object Model (POM) Design pattern** ensures a clean separation between test logic and element interactions, promoting **code reusability and maintainability**.  

**Selenium WebDriver**, integrated with **Java**, drives the browser automation, providing a robust solution for testing web applications.  

---

## Key Features  

- **Behavior-Driven Development (BDD):** Utilizes **Cucumber** to define test scenarios in a clear and understandable manner, fostering collaboration between technical and non-technical stakeholders.  
- **Page Object Model (POM):** Organizes web elements and interactions into reusable Page Objects, enhancing test readability, maintainability, and reusability.  
- **Java with Selenium WebDriver:** Provides a robust and flexible foundation for web browser automation.  
- **ElementUtil.java:** A powerful utility class for handling dynamic and complex web elements, ensuring stable and reliable test execution across different websites and scenarios.  
- **Cross-Browser Testing:** Supports testing across multiple browsers like **Chrome, Firefox, and Edge**, ensuring consistent application behavior.  
- **Parallel Test Execution:** Integrates with **TestNG** for efficient parallel execution of test cases.  
- **Robust Reporting:** Generates detailed and informative test reports for easy analysis.  

---

## ElementUtil.java: The Heart of Reliability  

The `ElementUtil.java` class is a core component of this framework, providing a collection of highly optimized and reusable methods for interacting with web elements and ensuring test stability in dynamic web environments.  

### Key Functionalities  

- **Smart Waiting Strategies:** Implements various waiting mechanisms, including:  
  - Explicit waits (`WebDriverWait` with `ExpectedConditions`)  
  - Fluent waits  
  - Customized waiting conditions to handle elements not immediately available in the DOM  

- **Locator Flexibility:** Offers methods to work with various locator strategies like **XPath, CSS selectors, ID, and class name**, providing flexibility in locating dynamic and complex elements.  

- **Handling Dynamic IDs/Classes:** Provides helper methods using partial attribute matching (e.g., `contains()`, `starts-with()` in XPath) to interact with elements having dynamically generated IDs or classes.  

- **JavaScript Executor Integration:** Allows executing JavaScript code for performing actions not directly supported by Selenium, such as:  
  - Scrolling  
  - Hovering  
  - Interacting with hidden elements  

- **Stale Element Handling:** Includes mechanisms to gracefully handle `StaleElementReferenceException` by re-locating the element if the page structure changes.  

- **Robustness against Pop-ups and Alerts:** Includes helper methods to handle pop-up windows and browser alerts, ensuring smooth test execution.  

- **Project Structure:**
- <img width="235" height="279" alt="image" src="https://github.com/user-attachments/assets/49dca5cb-0d39-4ad7-8751-c7b4a35151ef" />
 


