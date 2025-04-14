package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bittercode.model.Book;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

public class ViewBookServlet extends HttpServlet {
    BookService bookService = new BookServiceImpl();

    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = StoreUtil.initializeHtmlResponse(res);

        if (!StoreUtil.verifyCustomerLogin(req, res, pw)) {
            return;
        }

        StoreUtil.setupCustomerHomePage(req, res, pw, "books");

        try {
            List<Book> books = bookService.getAllBooks();
            StoreUtil.updateCartItems(req);

            HttpSession session = req.getSession();
            for (Book book : books) {
                pw.println(this.addBookToCard(session, book));
            }

            pw.println("</div>"
                    + "<div style='float:auto'><form action=\"cart\" method=\"post\">"
                    + "<input type='submit' class=\"btn btn-success\" name='cart' value='Proceed to Checkout'/></form>"
                    + "    </div>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addBookToCard(HttpSession session, Book book) {
        String bCode = book.getBarcode();
        int bQty = book.getQuantity();

        int cartItemQty = 0;
        if (session.getAttribute("qty_" + bCode) != null) {
            cartItemQty = (int) session.getAttribute("qty_" + bCode);
        }

        String button = "";
        if (bQty > 0) {
            button = "<form action=\"viewbook\" method=\"post\">"
                    + "<input type='hidden' name = 'selectedBookId' value = " + bCode + ">"
                    + "<input type='hidden' name='qty_" + bCode + "' value='1'/>"
                    + (cartItemQty == 0
                            ? "<input type='submit' class=\"btn btn-primary\" name='addToCart' value='Add To Cart'/></form>"
                            : "<form method='post' action='cart'>"
                                    + "<button type='submit' name='removeFromCart' class=\"glyphicon glyphicon-minus btn btn-danger\"></button> "
                                    + "<input type='hidden' name='selectedBookId' value='" + bCode + "'/>"
                                    + cartItemQty
                                    + " <button type='submit' name='addToCart' class=\"glyphicon glyphicon-plus btn btn-success\"></button></form>")
                    + "";
        } else {
            button = "<p class=\"btn btn-danger\">Out Of Stock</p>\r\n";
        }

        return "<div class=\"card\">\r\n"
                + "                <div class=\"row card-body\">\r\n"
                + "                    <img class=\"col-sm-6\" src=\"logo.png\" alt=\"Card image cap\">\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <h5 class=\"card-title text-success\">" + book.getName() + "</h5>\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        Author: <span class=\"text-primary\" style=\"font-weight:bold;\"> "
                + book.getAuthor()
                + "</span><br>\r\n"
                + "                        </p>\r\n"
                + "                        \r\n"
                + "                    </div>\r\n"
                + "                </div>\r\n"
                + "                <div class=\"row card-body\">\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        <span>Id: " + bCode + "</span>\r\n"
                + (bQty < 20 ? "<br><span class=\"text-danger\">Only " + bQty + " items left</span>\r\n"
                        : "<br><span class=\"text-success\">Trending</span>\r\n")
                + "                        </p>\r\n"
                + "                    </div>\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        Price: <span style=\"font-weight:bold; color:green\"> &#8377; "
                + book.getPrice()
                + " </span>\r\n"
                + "                        </p>\r\n"
                + button
                + "                    </div>\r\n"
                + "                </div>\r\n"
                + "            </div>";
    }
}
