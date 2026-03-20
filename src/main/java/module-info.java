module com.sahara {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    // Open packages to JavaFX reflection
    opens com.sahara to javafx.fxml;
    opens com.sahara.controller to javafx.fxml;
    opens com.sahara.model to javafx.base, javafx.fxml;

    // Export all packages
    exports com.sahara;
    exports com.sahara.controller;
    exports com.sahara.model;
    exports com.sahara.dao;
    exports com.sahara.util;
}
