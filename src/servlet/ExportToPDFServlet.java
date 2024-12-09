//package servlet;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.Calendar;
//import java.util.List;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
//
//import model.entity.WorkTime;
//
//@WebServlet("/exportToPDF")
//public class ExportToPDFServlet extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//
//        // PDF用の内容を取得する
//        String employeeName = (String) session.getAttribute("employeeName");
//        Calendar thisMonthCalendar = (Calendar) session.getAttribute("thisMonth");
//        List<WorkTime> workTimeThisMonthList = (List<WorkTime>) session.getAttribute("workTimeThisMonthList");
//
//        if (employeeName == null || thisMonthCalendar == null || workTimeThisMonthList == null) {
//            response.sendRedirect("attendance_menu.jsp");
//            return;
//        }
//
//        // HTMLコンテンツを作成
//        StringBuilder htmlContent = new StringBuilder();
//        htmlContent.append("<html><head><style>table {width: 100%; border-collapse: collapse;} th, td {border: 1px solid black; padding: 5px;}</style></head><body>");
//        htmlContent.append("<h1>タイムシート</h1>");
//        htmlContent.append("<p>名前: ").append(employeeName).append("</p>");
//        htmlContent.append("<p>").append(thisMonthCalendar.get(Calendar.YEAR)).append("年")
//                   .append(thisMonthCalendar.get(Calendar.MONTH) + 1).append("月分</p>");
//        htmlContent.append("<table><tr><th>日にち</th><th>出勤</th><th>退勤</th><th>休憩入り</th><th>休憩戻り</th><th>休憩時間</th><th>実働時間</th></tr>");
//
//        for (int i = 1; i <= thisMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
//            htmlContent.append("<tr><td>").append(thisMonthCalendar.get(Calendar.MONTH) + 1).append("月").append(i).append("日</td>");
//            boolean dataFound = false;
//
//            for (WorkTime workTime : workTimeThisMonthList) {
//                if (workTime.getWorkdate().getDayOfMonth() == i) {
//                    htmlContent.append("<td>").append(workTime.getStartTime() != null ? workTime.getStartTime().toString() : "").append("</td>");
//                    htmlContent.append("<td>").append(workTime.getFinishTime() != null ? workTime.getFinishTime().toString() : "").append("</td>");
//                    htmlContent.append("<td>").append(workTime.getBreakStartTime() != null ? workTime.getBreakStartTime().toString() : "").append("</td>");
//                    htmlContent.append("<td>").append(workTime.getBreakFinishTime() != null ? workTime.getBreakFinishTime().toString() : "").append("</td>");
//                    htmlContent.append("<td>").append(workTime.getBreakTime() != null ? workTime.getBreakTime().toString() : "").append("</td>");
//                    htmlContent.append("<td>").append(workTime.getWorkingHours() != null ? workTime.getWorkingHours().toString() : "").append("</td>");
//                    dataFound = true;
//                    break;
//                }
//            }
//            if (!dataFound) {
//                htmlContent.append("<td colspan='6'></td>");
//            }
//            htmlContent.append("</tr>");
//        }
//        htmlContent.append("</table></body></html>");
//
//        // PDFを生成してレスポンスに出力
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=timesheet.pdf");
//        try (OutputStream os = response.getOutputStream()) {
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.withHtmlContent(htmlContent.toString(), null);
//            builder.toStream(os);
//            builder.run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
