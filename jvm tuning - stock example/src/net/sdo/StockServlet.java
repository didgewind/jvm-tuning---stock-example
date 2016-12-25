/*
 * Copyright (c) 2013,2014 Scott Oaks. All rights reserved.
 */

package net.sdo;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sdo.stock.StockPriceHistory;
import net.sdo.stock.StockPriceUtils;
import net.sdo.stockimpl.MockStockPriceEntityManagerFactory;
import net.sdo.stockimpl.StockPriceHistoryImpl;
import net.sdo.stockimpl.StockPriceHistoryLogger;

public class StockServlet extends HttpServlet {

    private boolean doMock = System.getProperty("MockEntityManager") != null;
    private ThreadLocal<DateFormat> localDf = new ThreadLocal<DateFormat>() {
        @Override
        public DateFormat initialValue() {
            return DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        }
    };
    Set<SoftReference<StockPriceHistory>> softCache = new HashSet();
    Set<WeakReference<StockPriceHistory>> weakCache = new HashSet();

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
        throws ServletException, IOException {
        try {
            String symbol = request.getParameter("symbol");
            if (symbol == null) {
                symbol = StockPriceUtils.getRandomSymbol();
            }
            String startDate = request.getParameter("startDate");
            if (startDate == null) {
                startDate = "01/01/01";
            }
            String endDate = request.getParameter("endDate");
            if (endDate == null) {
                endDate = "12/31/25";
            }
            String log = request.getParameter("log");
            int impl = StockPriceHistory.STANDARD;
            if ("true".equals(log)) {
                impl = StockPriceHistory.LOGGING;
            }
            EntityManagerFactory emf = new MockStockPriceEntityManagerFactory("MockEntityManager");
            EntityManager em = emf.createEntityManager();
            StockPriceHistory sph;
            DateFormat df = localDf.get();
            if (impl == StockPriceHistory.STANDARD) {
                sph = new StockPriceHistoryImpl(symbol, df.parse(startDate), df.parse(endDate), em);
            }
            else {
                sph = new StockPriceHistoryLogger(symbol, df.parse(startDate), df.parse(endDate), em);
            }
            String saveSession = request.getParameter("save");
            if (saveSession != null) {
                int saveCount = Integer.parseInt(saveSession);
                HttpSession session = request.getSession(true);
                ArrayList<StockPriceHistory> al =
                    (ArrayList<StockPriceHistory>) session.getAttribute("saveHistory");
                if (al == null) {
                    al = new ArrayList<StockPriceHistory>(100);
                }
                al.add(sph);
                if (al.size() > saveCount) {
                    al.remove(0);
                }
                ArrayList<Date> ald = (ArrayList<Date>) session.getAttribute("date");
                if (ald == null) {
                    ald = new ArrayList<Date>(100);
                }
                ald.add(new Date());
                if (ald.size() > saveCount * 4) {
                    ald.remove(0);
                }
                session.setAttribute("saveHistory", al);
                session.setAttribute("last", ald);
            }
            String doSoftCache = request.getParameter("saveSoft");
            if ("true".equals(doSoftCache)) {
                softCache.add(new SoftReference(sph));
            }
            String doWeakCache = request.getParameter("saveWeak");
            if ("true".equals(doWeakCache)) {
                weakCache.add(new WeakReference(sph));
            }
            request.setAttribute("history", sph);
            String doLong = request.getParameter("long");
            if ("true".equals(doLong)) {
                request.getRequestDispatcher("longhistory.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("history.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("exception", e);
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample StockPrice Servlet";
    }
}
