# Lab 11: Cookie Clicker

In this lab, you will build a small Cookie Clicker game. The game is simple on purpose: it lets you focus on the relationships among an FXML view, a Java controller, and Java model objects. These are the same building blocks you can reuse in a JavaFX project of your own.

## Learning goals

By the end of the lab, you should be able to:

- connect an FXML event to a controller method with `onAction`;
- inject an FXML component into a controller with `fx:id` and `@FXML`;
- load an image from the Maven resources directory and display it in an FXML view;
- explain the roles of the view, controller, and model;
- use an abstract class, inheritance, and polymorphism for related model objects; and
- use a supplied JavaFX `Timeline` to update application state and refresh the view.

## Before you begin

Run the starter application: [CookieClickerApp.java](src/main/java/math130/gui/CookieClickerApp.java)

You should see a window with a label and a **Click Me!** button. It intentionally does nothing yet. Work through the parts in order and run the application after each checkpoint.

> The starter has an FXML view and a controller class already connected by `fx:controller`. You will add the remaining connections yourself.

## The Model-View-Controller (MVC) Pattern

This lab is an example of the Model-View-Controller (MVC) pattern, which organizes code by separating data handling, user interface display, and request processing.

- **View:** `cookie-clicker-view.fxml` contains visual components such as labels, buttons, and images.
- **Controller:** `CookieClickerController` responds to user actions and updates the view.
- **Model:** `Upgrade`, `Cursor`, and `Grandma` represent game data and rules. Model classes must not import JavaFX.

---

## Part 1A: A button event reaches the controller

**Goal:** See that FXML can call a controller method.

1. In `CookieClickerController.java`, add this method inside the class:

   ```java
   @FXML
   protected void handleClick() {
       System.out.println("The button was clicked.");
   }
   ```

2. In `cookie-clicker-view.fxml`, add an `onAction` attribute to the button:

   ```xml
   <Button text="Click Me!" onAction="#handleClick"/>
   ```

3. Run the application and click the button.

**Checkpoint:** Every click prints `The button was clicked.` in the Run console.

**What happened?** `onAction="#handleClick"` tells JavaFX to call `handleClick()` on the controller named by `fx:controller`. `@FXML` permits the FXML loader to call this method.

---

## Part 1B: The controller changes a label

**Goal:** Use `fx:id` and `@FXML` to give the controller a reference to a view component.

1. Add a counter field and an injected label field to the controller:

   ```java
   private int cookieCount = 0;

   @FXML
   private Label cookieLabel;
   ```

   Import `javafx.scene.control.Label`.

2. Give the label in FXML the matching id:

   ```xml
   <Label fx:id="cookieLabel" text="Cookies: 0"/>
   ```

3. Replace the print statement in `handleClick()` with:

   ```java
   cookieCount++;
   cookieLabel.setText("Cookies: " + cookieCount);
   ```

**Checkpoint:** The label changes from `Cookies: 0` to `Cookies: 1`, `Cookies: 2`, and so on.

**Key distinction:** `onAction` connects a view event to a controller method. `fx:id` plus `@FXML` connects a specific view component to a controller field. The controller can now use that `Label` object.

---

## Part 1C: Add a cookie image from the resources directory

**Goal:** Add an image to the Maven resources directory and display it inside the existing button.

A standard Maven project keeps Java source files and application resources in separate directories:

```text
src
└── main
    ├── java
    │   └── math130
    │       └── gui
    └── resources
        └── math130
            └── gui
                ├── cookie-clicker-view.fxml
                └── images
                    └── cookie.png
```

1. Choose a cookie image and save it as `cookie.png`.

2. Create an `images` directory inside:

   ```text
   src/main/resources/math130/gui
   ```

3. Place `cookie.png` inside the new directory:

   ```text
   src/main/resources/math130/gui/images/cookie.png
   ```

4. Add these imports near the top of `cookie-clicker-view.fxml`:

   ```xml
   <?import javafx.scene.image.Image?>
   <?import javafx.scene.image.ImageView?>
   ```

5. Replace the text button with a button that uses the image as its graphic:

   ```xml
   <Button onAction="#handleClick">
       <graphic>
           <ImageView fitWidth="150" preserveRatio="true">
               <image>
                   <Image url="@images/cookie.png"/>
               </image>
           </ImageView>
       </graphic>
   </Button>
   ```

The `@images/cookie.png` path is relative to the FXML file. The control is still a `Button`, so the `onAction` connection from Part 1A continues to work.

**Checkpoint:** The cookie image appears in the application, and clicking it still increases the cookie count.

---

## Part 2A: Create the upgrade model

**Goal:** Put upgrade rules in Java model classes, separate from JavaFX.

Create an abstract `Upgrade` class in the `math130.gui` package. Use this structure:

```java
public abstract class Upgrade {
    private final String name;
    private final int baseCost;
    private final int cookiesPerSecondEach;
    private int quantityOwned;

    protected Upgrade(String name, int baseCost, int cookiesPerSecondEach) {
        this.name = name;
        this.baseCost = baseCost;
        this.cookiesPerSecondEach = cookiesPerSecondEach;
    }

    public String getName() {
        return name;
    }

    public int getQuantityOwned() {
        return quantityOwned;
    }

    public void purchase() {
        quantityOwned++;
    }

    public int getTotalCookiesPerSecond() {
        return cookiesPerSecondEach * quantityOwned;
    }

    protected int getBaseCost() {
        return baseCost;
    }

    public abstract int getCurrentCost();
}
```

The production rule is:

```text
cookies per second from an upgrade = production of one item × quantity owned
```

Create a concrete `Cursor` class that extends `Upgrade`:

```java
public class Cursor extends Upgrade {

    public Cursor() {
        super("Cursor", 15, 1);
    }

    @Override
    public int getCurrentCost() {
        return (int) Math.ceil(
                getBaseCost() * (1 + 0.10 * getQuantityOwned()));
    }
}
```

This is linear scaling: each owned Cursor adds 10% of the original base cost. The first Cursor costs its base cost because its quantity is zero.

In the controller, create a Cursor object and an upgrade collection:

```java
private final Upgrade cursor = new Cursor();
private final List<Upgrade> upgrades = List.of(cursor);
```

Import `java.util.List`.

---

## Part 2B: Buy an upgrade and calculate total production

**Goal:** Let the controller coordinate purchases while the model supplies the rules.

1. Add a Cursor purchase button to FXML:

   ```xml
   <Button onAction="#buyCursor" text="Buy Cursor"/>
   ```

2. Add this method to the controller:

   ```java
   @FXML
   protected void buyCursor() {
       int cost = cursor.getCurrentCost();

       if (cookieCount >= cost) {
           cookieCount -= cost;
           cursor.purchase();
       }

       refreshView();
   }
   ```

3. Add a method that calculates total production from every object in the upgrade list:

   ```java
   private int getTotalCookiesPerSecond() {
       return upgrades.stream()
               .mapToInt(Upgrade::getTotalCookiesPerSecond)
               .sum();
   }
   ```

4. Add the production label immediately below the cookie label in FXML:

   ```xml
   <Label fx:id="cookiesPerSecondLabel"
          text="Cookies per second: 0"/>
   ```

5. Inject the new label into the controller:

   ```java
   @FXML
   private Label cookiesPerSecondLabel;
   ```

6. Add one method that refreshes both labels:

   ```java
   private void refreshView() {
       cookieLabel.setText("Cookies: " + cookieCount);
       cookiesPerSecondLabel.setText(
               "Cookies per second: " + getTotalCookiesPerSecond());
   }
   ```

7. Change `handleClick()` so it updates the state and then refreshes the view:

   ```java
   @FXML
   protected void handleClick() {
       cookieCount++;
       refreshView();
   }
   ```

Call `refreshView()` at the end of every controller method that changes game state.

**Checkpoint:** Buying a Cursor deducts its current cost, increases its owned quantity, and increases cookies per second by 1.

**MVC note:** The controller decides whether the player can afford an upgrade because it currently owns `cookieCount`. The upgrade model knows its own price, quantity, and production rule. Neither model class knows about labels, buttons, or images.

---

## Part 2C: Provided passive-production timer

**Goal:** Observe production over time without learning JavaFX animation details yet.

Add these imports to the controller:

```java
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
```

Add this field:

```java
private Timeline timeline;
```

Then add this code exactly as written. It is provided infrastructure for this lab; you are not expected to create it from scratch.

```java
@FXML
public void initialize() {
    timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
        cookieCount += getTotalCookiesPerSecond();
        refreshView();
    }));

    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}
```

**What it does:** Once per second, JavaFX asks the controller to add all upgrade production to the cookie count and then refresh the labels. `Timeline` runs on the JavaFX Application Thread, so it can safely update the view.

**Checkpoint:** After buying a Cursor, the cookie count increases automatically by 1 every second.

---

## Part 2D: Add another upgrade with inheritance and polymorphism

**Goal:** Add a new upgrade type without rewriting the total-production algorithm.

### Step 1: Create the `Grandma` subclass

Create a `Grandma` class that extends `Upgrade`.

- Name: `Grandma`
- Base cost: `100`
- Production: `5` cookies per second each
- Cost rule: increase the current cost by 15% for each Grandma already owned

```java
public class Grandma extends Upgrade {

    public Grandma() {
        super("Grandma", 100, 5);
    }

    @Override
    public int getCurrentCost() {
        return (int) Math.ceil(
                getBaseCost() * Math.pow(1.15, getQuantityOwned()));
    }
}
```

### Step 2: Create the object in the controller

Add a `Grandma` object beside the existing Cursor object:

```java
private final Upgrade cursor = new Cursor();
private final Upgrade grandma = new Grandma();
```

Update the list so it contains both objects:

```java
private final List<Upgrade> upgrades = List.of(cursor, grandma);
```

Do not change `getTotalCookiesPerSecond()`. It already processes every object in `List<Upgrade>`.

### Step 3: Add a purchase button

Add a Grandma purchase button to FXML:

```xml
<Button onAction="#buyGrandma" text="Buy Grandma"/>
```

### Step 4: Add the purchase handler

Add this method to the controller:

```java
@FXML
protected void buyGrandma() {
    int cost = grandma.getCurrentCost();

    if (cookieCount >= cost) {
        cookieCount -= cost;
        grandma.purchase();
    }

    refreshView();
}
```

### Step 5: Run and test the application

Confirm that:

- the first Grandma costs 100 cookies;
- buying a Grandma deducts its current cost;
- each Grandma adds 5 cookies per second; and
- the cost increases after each purchase.

**Checkpoint:** The controller calculates total production from both the Cursor and Grandma objects without checking which subclass each object belongs to.

**Why this is polymorphism:** Both variables have the declared type `Upgrade`, but each refers to a different subclass object. When the controller calls `getCurrentCost()`, Java runs the implementation that belongs to the object's actual class.

The upgrade list also demonstrates subtype substitutability: `Cursor` and `Grandma` objects can both be stored and processed as `Upgrade` objects.

---

## Going Further: Decouple the game state from the controller

This lab deliberately keeps `cookieCount` in the controller to keep the first MVC example approachable. For a larger application or final project, create a `GameState` model class that owns the cookie count and the upgrade list.

Move operations such as `addCookies`, `canAfford`, `buyUpgrade`, and `getTotalCookiesPerSecond` into that class. The controller would then respond to an event, call the model, and refresh the view. This is an opportunity to practice a more fully decoupled MVC design in your final project.

Other extensions:

- show the current cost and quantity owned for each upgrade;
- show a message when an upgrade is unaffordable;
- add a new subclass such as `Farm`, `Mine`, or `Factory`;
- add a progress bar or achievement based on model state; or
- save and load the game state.
