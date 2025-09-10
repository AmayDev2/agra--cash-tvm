package com.amay.tom;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;

public class KeypadHandler {

    private boolean escPressed = false;

    public void attach(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, this::handleKeyRelease);
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        switch (code) {
            case F1 -> {
                // Map F1 → Tab
                event.consume();
                traverseNext(event.getTarget(), true);
            }
            case CLEAR -> {
                // Map Clear → Backspace
                event.consume();
                System.out.println("Clear pressed → Backspace action");
                fireKey(event, KeyCode.BACK_SPACE);
            }
            case ESCAPE -> {
                escPressed = true;
                event.consume();
            }
            case ENTER -> {
                if (escPressed) {
                    // Esc+Enter → Ctrl+Enter
                    event.consume();
                    System.out.println("Esc+Enter → Ctrl+Enter action");
                    fireCtrlEnter(event);
                } else {
                    System.out.println("Enter pressed → Submit");
                }
            }
            case DIGIT1 -> {
                if (escPressed) {
                    event.consume();
                    System.out.println("Esc+1 → F2 action");
                    fireKey(event, KeyCode.F2);
                }
            }
            case DIGIT2 -> {
                if (escPressed) {
                    event.consume();
                    System.out.println("Esc+2 → F3 action");
                    fireKey(event, KeyCode.F3);
                }
            }
            // Add more combos here...
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            escPressed = false;
        }
    }

    /** Move focus forward/backward (instead of impl_traverse) */
    private void traverseNext(Object target, boolean forward) {
        if (target instanceof Node node && node.getScene() != null) {
            KeyEvent tabEvent = new KeyEvent(
                    KeyEvent.KEY_PRESSED,
                    "",
                    "",
                    KeyCode.TAB,
                    false,
                    !forward, // shift=true → backward
                    false,
                    false
            );
            node.fireEvent(tabEvent);
        }
    }

    /** Helper: Fire a normal key event (e.g. Backspace, F2, F3) */
    private void fireKey(KeyEvent original, KeyCode code) {
        if (original.getTarget() instanceof Node node) {
            KeyEvent keyEvent = new KeyEvent(
                    KeyEvent.KEY_PRESSED,
                    code.getChar(),
                    code.getName(),
                    code,
                    false,
                    false,
                    false,
                    false
            );
            node.fireEvent(keyEvent);
        }
    }

    /** Helper: Fire Ctrl+Enter combo */
    private void fireCtrlEnter(KeyEvent original) {
        if (original.getTarget() instanceof Node node) {
            KeyEvent keyEvent = new KeyEvent(
                    KeyEvent.KEY_PRESSED,
                    "\r",
                    "Enter",
                    KeyCode.ENTER,
                    false,
                    true,   // control pressed
                    false,
                    false
            );
            node.fireEvent(keyEvent);
        }
    }
}
