package Debug;

public class DebugLogger {

    // デバッグ用関数：ファイル名・行番号とメッセージを出力
    public static void log(String message) {
        // スタックトレースから呼び出し元の情報を取得
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];  // 呼び出し元のスタック

        String logMessage = String.format(
            "DEBUG [%s:%d] %s", 
            caller.getFileName(), caller.getLineNumber(), message
        );

        // デバッグメッセージを標準出力に出力
        System.out.println(logMessage);
    }
}
