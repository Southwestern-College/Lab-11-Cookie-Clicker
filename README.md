
![cookie clicker](https://img.icons8.com/emoji/96/000000/cookie-emoji.png)

# Lab 11: Cookie Clicker - JavaFX & MVC Pattern

Welcome to Cookie Clicker! In this lab, you'll build a simplified version of the popular incremental game while learning fundamental GUI concepts and the Model-View-Controller (MVC) design pattern using JavaFX.

## Learning Objectives

By the end of this lab, you will:
- Understand how Controllers interact with Views in JavaFX
- Learn to link FXML components using `fx:id`
- Implement the Model-View-Controller (MVC) pattern
- Practice Object-Oriented Programming concepts: Abstraction, Inheritance, and Polymorphism

---

## Part 1: Basic Cookie Clicker (View ↔ Controller Interaction)

In Part 1, you'll create a simple clicker where clicking a button increments a cookie counter.

### Part 1A: Click Counter

**Goal**: Understand how the Controller handles events and updates the View.

#### To-Dos:
1. **Modify `CookieClickerController.java`**:
    - Add an instance variable `cookieCount` to track the number of cookies
    - In the `handleClick()` method:
        - Increment `cookieCount` each time the button is clicked
        - Update the `cookieLabel` to display the current count
        - Format: `"Cookies: X"` (where X is the count)

**Expected Behavior**:
- When you click the "Click Me!" button, the label should update: `Cookies: 1`, `Cookies: 2`, etc.

---

### Part 1B: Image Button with fx:id

**Goal**: Learn how `fx:id` links FXML components to Controller variables.

#### To-Dos:
1. **Download a cookie image**:
    - Find a cookie image online (PNG recommended, ~100-200px)
    - Save it in `src/main/resources/math130/gui/images/`
    - Name it `cookie.png`

2. **Modify `cookie-clicker-view.fxml`**:
    - Replace the `<Button>` with an `<ImageView>` wrapped in a `<Button>`
    - Give the button an `fx:id="cookieButton"`
    - Set the `onAction` to call the controller method

3. **Update `CookieClickerController.java`**:
    - Add `@FXML private Button cookieButton;` to link the FXML button
    - No other changes needed—it should work the same way!

**Expected Behavior**:
- Clicking the cookie image increments the counter

**Hint**: Your button in FXML should look something like:
```xml
<Button fx:id="cookieButton" onAction="#handleClick">
    <graphic>
        <ImageView fitWidth="150" fitHeight="150">
            <Image url="@images/cookie.png"/>
        </ImageView>
    </graphic>
</Button>
```

---

## Part 2: Full MVC + Object-Oriented Design

In Part 2, you'll implement a complete Cookie Clicker game using the MVC pattern and demonstrate key OOP concepts.

### Part 2A: Create the Model

**Goal**: Implement the Model in the MVC pattern by creating an `Upgrade` class.

#### To-Dos:
1. **Create `Upgrade.java` class** in the same package:
   ```java
   public class Upgrade {
       private String name;
       private int cost;
       private int cookiesPerSecond;
       private int quantityOwned;
       
       // Constructor, getters, setters, and methods go here
   }
   ```

2. **Add methods**:
    - `purchase()`: Increases `quantityOwned` by 1
    - `getCurrentCost()`: Returns the cost for the next purchase
    - `getTotalProduction()`: Returns `cookiesPerSecond * quantityOwned`

3. **Update Controller**:
    - Add an `Upgrade` object (e.g., `Upgrade cursor = new Upgrade("Cursor", 15, 1, 0)`)
    - Add a button in FXML to purchase the upgrade
    - When purchased: deduct cookies, call `upgrade.purchase()`, update display

**Expected Behavior**:
- Display upgrade info: "Cursor - Cost: 15"
- When clicked (if you have enough cookies):
    - Deduct 15 cookies
    - Increase quantity owned
    - Show "Cursor (1 owned)"

---

### Part 2B: Automatic Cookie Generation

**Goal**: Implement passive cookie generation using a Timeline (JavaFX timer).

#### To-Dos:
1. **Add Timeline to Controller**:
   ```java
   private Timeline timeline;
   ```

2. **In `initialize()` method**:
    - Create a Timeline that runs every second
    - Calculate total cookies per second from all upgrades
    - Add that amount to `cookieCount`

3. **Display cookies per second**:
    - Add a label showing "Cookies per second: X"

**Expected Behavior**:
- After purchasing a Cursor, cookies automatically increment every second

---

### Part 2C: Abstraction & Inheritance

**Goal**: Demonstrate OOP principles with multiple upgrade types.

#### To-Dos:
1. **Refactor `Upgrade` to abstract class**:
    - Make `getCurrentCost()` abstract
    - Different upgrades can have different cost scaling formulas

2. **Create concrete subclasses**:
    - `Cursor extends Upgrade`: Linear cost (base cost + 1.1*quantity)
    - `Grandma extends Upgrade`: Exponential cost (base cost * 1.25^quantity)
    - `Farm extends Upgrade`: Different cost formula

3. **Use Polymorphism**:
    - Store all upgrades in `ArrayList<Upgrade> upgrades`
    - Loop through them to calculate total production
    - Display all upgrades dynamically

**Expected Behavior**:
- Multiple upgrade types with different costs and production rates
- Each upgrade's cost increases differently when purchased

---

## Tips

- **Part 1**: Focus on understanding how changing variables in the Controller updates the View
- **Part 2**: Think about separation of concerns—the Model (`Upgrade`) shouldn't know about JavaFX
- Use `String.format()` for clean number formatting: `String.format("Cookies: %,d", cookieCount)`
- Test frequently! Run after each small change

## Resources

- [JavaFX Documentation](https://openjfx.io/javadoc/21/)
- [JavaFX Button](https://openjfx.io/javadoc/21/javafx.controls/javafx/scene/control/Button.html)
- [JavaFX ImageView](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/image/ImageView.html)
- [JavaFX Timeline](https://openjfx.io/javadoc/21/javafx.graphics/javafx/animation/Timeline.html)
- Original Cookie Clicker: [orteil.dashnet.org/cookieclicker](https://orteil.dashnet.org/cookieclicker/)

## Bonus Challenges

- Add more upgrade types (Mine, Factory, Bank)
- Implement upgrade tooltips showing production rates
- Add sound effects when clicking
- Save/load game progress to a file
- Add achievements

---

Good luck, and happy clicking! 🍪