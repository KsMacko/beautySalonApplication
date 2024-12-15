module com.example.salonclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires org.json;
    requires static lombok;

    opens com.example.salonclient.Controllers.Client to javafx.fxml;
    opens com.example.salonclient.Controllers.Admin to javafx.fxml;
    opens com.example.salonclient.Model.BasicClasses to com.google.gson, javafx.base;
    opens com.example.salonclient.Controllers.Admin.Cards to javafx.fxml;
    exports com.example.salonclient;
    exports com.example.salonclient.Controllers.Admin.Cards;
    opens com.example.salonclient.Model to com.google.gson;
    opens com.example.salonclient.Controllers.Client.Cards to javafx.fxml;
}