module rs.cs.restaurantnea {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires mail;
    requires activation;

    opens rs.cs.restaurantnea to javafx.fxml;
    exports rs.cs.restaurantnea;
    exports rs.cs.restaurantnea.general.objects;
    opens rs.cs.restaurantnea.general.objects to javafx.fxml;
    exports rs.cs.restaurantnea.customerArea;
    opens rs.cs.restaurantnea.customerArea to javafx.fxml;
    exports rs.cs.restaurantnea.LISU;
    opens rs.cs.restaurantnea.LISU to javafx.fxml;
    opens rs.cs.restaurantnea.adminArea to javafx.fxml;
    exports rs.cs.restaurantnea.adminArea;
}