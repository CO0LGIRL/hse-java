package hse.java.commander;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class MainController {

    private enum FileOperation {
        COPY, MOVE, DELETE
    }

    @FXML public ListView<String> left;
    @FXML public ListView<String> right;

    @FXML public Button move;
    @FXML public Button copy;
    @FXML public Button delete;

    private Path leftDir;
    private Path rightDir;

    private boolean isLeftActive = true;

    // for testing
    public void setInitialDirs(Path leftStart, Path rightStart) {
        this.leftDir = leftStart;
        this.rightDir = rightStart;
        refreshPanels();
    }

    private void updateList(Path dir, ListView<String> dir_list) {
        dir_list.getItems().clear();
        dir_list.getItems().add("...");

        if (dir != null) {
            try (java.nio.file.DirectoryStream<Path> dirStream = java.nio.file.Files.newDirectoryStream(dir)) {
                for (Path path : dirStream) {
                    dir_list.getItems().add(path.getFileName().toString());
                }
            } catch (java.io.IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void initialize() {
        if (leftDir == null) leftDir = Path.of(System.getProperty("user.home"));
        if (rightDir == null) rightDir = Path.of(System.getProperty("user.home"));

        refreshPanels();

        left.setOnMouseClicked(event -> handlePanelClick(event, left, true));
        right.setOnMouseClicked(event -> handlePanelClick(event, right, false));

        if (copy != null) copy.setOnAction(e -> executeOperation(FileOperation.COPY));
        if (move != null) move.setOnAction(e -> executeOperation(FileOperation.MOVE));
        if (delete != null) delete.setOnAction(e -> executeOperation(FileOperation.DELETE));
    }

    private void handlePanelClick(MouseEvent event, ListView<String> list, boolean isLeft) {
        isLeftActive = isLeft;

        if (isLeft) {
            right.getSelectionModel().clearSelection();
        } else {
            left.getSelectionModel().clearSelection();
        }

        if (event.getClickCount() == 2) {
            String selected = list.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            Path currentDir = isLeft ? leftDir : rightDir;
            Path newDir;

            if (selected.equals("...")) {
                newDir = currentDir.getParent();
            } else {
                newDir = currentDir.resolve(selected);
            }

            if (newDir != null && Files.isDirectory(newDir)) {
                if (isLeft) leftDir = newDir; else rightDir = newDir;
                refreshPanels();
            }
        }
    }

    private void executeOperation(FileOperation op) {
        ListView<String> activeList = isLeftActive ? left : right;
        Path sourceDir = isLeftActive ? leftDir : rightDir;
        Path targetDir = isLeftActive ? rightDir : leftDir;

        String selected = activeList.getSelectionModel().getSelectedItem();

        if (selected == null || selected.equals("...")) return;

        Path sourcePath = sourceDir.resolve(selected);

        try {
            switch (op) {
                case COPY -> {
                    Path targetPath = targetDir.resolve(selected);
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                case MOVE -> {
                    Path targetPath = targetDir.resolve(selected);
                    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                case DELETE -> {
                    Files.delete(sourcePath);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        refreshPanels();
    }

    private void refreshPanels() {
        updateList(leftDir, left);
        updateList(rightDir, right);
    }
}

