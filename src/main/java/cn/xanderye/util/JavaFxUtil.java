package cn.xanderye.util;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created on 2020/9/2.
 *
 * @author XanderYe
 */
@Slf4j
public class JavaFxUtil {

    private static TextArea logArea = null;

    /**
     * 日志方法首先需要在Controller的initialize中执行
     * @param textArea
     * @return void
     * @author XanderYe
     * @date 2020/9/4
     */
    public static void initLog(TextArea textArea) {
        logArea = textArea;
    }

    /**
     * 打印日志
     * @param pattern
     * @param args
     * @return void
     * @author XanderYe
     * @date 2020/9/4
     */
    public static void log(String pattern, String...args) {
        String msg = MessageFormatter.arrayFormat(pattern, args).getMessage();
        log.info(msg);
        msg = msg + "\r\n";
        if (logArea != null) {
            String finalMsg = msg;
            Platform.runLater(() -> logArea.appendText(finalMsg));
        }
    }

    /**
     * 确认弹窗
     * @param header
     * @param message
     * @return boolean
     * @author XanderYe
     * @date 2020/9/2
     */
    public static boolean confirmDialog(String header, String message) {
        ButtonType confirm = new ButtonType("确定", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("取消", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, confirm, cancel);
        alert.setTitle("确认");
        alert.setHeaderText(header);
        Optional<ButtonType> buttonType = alert.showAndWait();
        return buttonType.orElse(cancel).getButtonData().equals(ButtonBar.ButtonData.YES);
    }

    /**
     * 信息弹窗
     * @param header
     * @param message
     * @return boolean
     * @author XanderYe
     * @date 2020/9/2
     */
    public static void alertDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("信息");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * 错误弹窗
     * @param header
     * @param message
     * @return boolean
     * @author XanderYe
     * @date 2020/9/2
     */
    public static void errorDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }
    /**
     * 警告弹窗
     * @param header
     * @param message
     * @return boolean
     * @author XanderYe
     * @date 2020/9/2
     */
    public static void warnDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * 列表设置展示字段
     * @param listView listView对象
     * @param function 函数式方法实现返回字段值
     * @return void
     * @author XanderYe
     * @date 2021/10/13
     */
    public static <T> void listViewConverter(ListView<T> listView, Function<T, Object> function) {
        listView.setCellFactory(param -> new ListCell<T>(){
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || function.apply(item) == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(function.apply(item)));
                }
            }
        });
    }

    /**
     * 下拉设置展示字段
     * @param comboBox comboBox对象
     * @param function 函数式方法实现返回字段值
     * @return void
     * @author XanderYe
     * @date 2021/10/13
     */
    public static <T> void comboBoxConverter(ComboBox<T> comboBox, Function<T, Object> function) {
        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object != null) {
                    return String.valueOf(function.apply(object));
                }
                return null;
            }
            @Override
            public T fromString(String string) {
                for (T item : comboBox.getItems()) {
                    if (item != null && string.equals(function.apply(item))) {
                        return item;
                    }
                }
                return null;
            }
        });
    }
}
