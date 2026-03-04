module lai.seven {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    opens lai.seven to javafx.fxml;
    exports lai.seven;
}
