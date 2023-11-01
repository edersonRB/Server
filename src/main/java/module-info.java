module sistemasdistribuidos.cliente {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires json;
    requires org.apache.commons.codec;
    requires jjwt.api;
    requires jjwt.impl;

    opens sistemasdistribuidos.servidor to javafx.fxml;
    exports sistemasdistribuidos.servidor;
}