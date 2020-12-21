package PopUpWindow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ExceptionWindow {

    public void start(String exceptionMessage){
        Stage stage = new Stage();
        Scene scene = new Scene(prepareLayout(exceptionMessage));
        stage.setTitle("Error");
        stage.setScene(scene);
        stage.show();
    }

    private VBox prepareLayout(String exceptionMessage){
        Text dangerText = new Text("Error! Program find an exception \n Please, repair it.");
        dangerText.setFont(new Font(20));
        dangerText.setTextAlignment(TextAlignment.CENTER);

        Text exceptionText = new Text(exceptionMessage);
        exceptionText.setFont(new Font(20));

        VBox vbox = new VBox(dangerText,exceptionText);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 50, 30, 50));
        return vbox;
    }
}
