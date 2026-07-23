package math130.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CookieClickerController {
    @FXML
    private Label cookieLabel;

    @FXML
    protected void handleClick() {
        cookieLabel.setText("Cookies: X");
    }
}