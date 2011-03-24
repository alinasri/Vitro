<html>
<%@ page isThreadSafe = "false" %>
<%@ page import="java.util.*,java.lang.String.*" %>
<head>
<title>Query Results</title>
<link rel="stylesheet" type="text/css" href="css/global.css.jsp"/>
<%
String headerStr = (String)request.getAttribute("header");
if ( headerStr == null || (!headerStr.equalsIgnoreCase("noheader")) ) { %>
<jsp:include page="header.jsp" flush="true" >
	<jsp:param name="header" value="<%=headerStr%>" />
</jsp:include>
<%	} %>
</head>
<BODY leftmargin="0" topmargin="0" marginheight="0" marginwidth="0">
<jsp:useBean id="results" class="java.util.ArrayList" scope="request" />
<jsp:useBean id="loginHandler" class="formbeans.LoginFormBean" scope="session"/>
<!-- ENCLOSING TABLE TO PUT 1 row of space at top and 1 along left edge -->
<table border="0" cellpadding="0" cellspacing="0" width = "100%">
<tr><td colspan="2" class="whitebackground"><img src="icons/transparent.gif" height="1"></td></tr>
<tr>
<td class="whitebackground"><img src="icons/transparent.gif" width="1" height="1"></td>
<td>
<%
int rows = 0;
    
String editFormStr = (String)request.getAttribute("editform");
String minEditRoleStr = (String)request.getAttribute("min_edit_role");
	
String firstValue = "null";
Integer columnCount = (Integer)request.getAttribute("columncount");
rows = columnCount.intValue();
	
String clickSortStr = (String)request.getAttribute("clicksort");
	
if ( rows > 0  && results.size() > rows ) { // avoid divide by zero error in next statement
	String suppressStr = null;
	int columns = results.size() / rows;
	if ( ( suppressStr = (String)request.getAttribute("suppressquery")) == null ) { // only inserted into request if true %>
		<p><i><b><%= columns - 1 %></b> results were retrieved in <b><%= rows %></b> rows for query "<%=request.getAttribute("querystring")%>".</i></p>
<%	} 
	if ( clickSortStr != null && clickSortStr.equals("true")) {
		if ( columns > 2 ) { %>
			<p><i>Click on the row header to sort columns by that row.</i></p>
<%		}
	}  %>
	<table border=0 cellpadding=2 cellspacing=0 width=100%>
<%	String[] resultsArray = new String[results.size()]; // see Core Java Vol. 1 p.216
	results.toArray( resultsArray ); 
	firstValue = resultsArray[ rows ];
	//secondValue= resultsArray[ rows + 1 ];
	String classString = "";
	boolean[] postQHeaderCols = new boolean[ columns ];
	for ( int eachcol=0; eachcol < columns; eachcol++ ) {
		postQHeaderCols[ eachcol ] = false;
	}
	for ( int thisrow = 0; thisrow < rows; thisrow++ ) {
		//int currentPostQCol = 0;
		boolean dropRow = false;
    		for ( int thiscol = 0; thiscol < columns; thiscol++ ) {
			String thisResult= resultsArray[ (rows * thiscol) + thisrow ];
			if ( thisResult.equals("+")) { /* occurs all in first row, so postQHeaderCols should be correctly initialized */
				classString = "postheaderright";
				postQHeaderCols[ thiscol ] = true;
				//++currentPostQCol;
				thisResult="&nbsp;";
			} else if ( thisResult.indexOf("@@")== 0) {
				classString="postheadercenter";
				thisResult ="query values"; //leave as follows for diagnostics: thisResult.substring(2);
				thisResult = thisResult.substring(2);
			} else {
				if ( postQHeaderCols[ thiscol ] == true )
					classString = "postheaderright";
				else if ( thiscol == 1 && thisrow < 2 )
					classString = "rowbold";
				else
					classString = "row";
				if ( thisResult.equals(""))
					thisResult="&nbsp;";
			}
			if ( thiscol == 0 ) { // 1st column of new row
				if ( thisrow > 0 ) { // first must close prior row %>
					</tr>
<%					if (thisResult.equals("XX")) {
						dropRow = true;
					} %>
					<tr valign="top" class="rowvert">  <!-- okay to start even a dropRow because it will get no <td> elements -->
<%				} else { %>
					<tr valign="top" class="header">  <!-- okay to start even a dropRow because it will get no <td> elements -->
<%				}
				if ( !dropRow ) { %>
					<td width="10%" class="verticalfieldlabel">
					<%=thisResult%>
<%				}
			} else { // 2nd or higher column
				if ( !dropRow ) { %>
					<td class=<%=classString%> >
<%					if (thisResult.equals("XX")) { %>
						<%="&nbsp;"%>
<%					} else { %>
						<%=thisResult%>
<%					}
				}
   			}
			if ( !dropRow ) { %>
				</td>
<%			}
		}
   	}  %>
	</tr>
	</table>
<%	
} else { 
	System.out.println("No results reported when " + rows + " rows and a result array size of " + results.size()); %>
	No results retrieved for query "<%=request.getAttribute("querystring")%>".
<%	Iterator errorIter = results.iterator();
	while ( errorIter.hasNext()) {
		String errorResult = (String)errorIter.next(); %>
		<p>Error returned: <%= errorResult%></p>		
<%	}
} %>
</td>
</tr>
</table>

<%
boolean showFooter=false;
if ( headerStr == null || (!headerStr.equalsIgnoreCase("noheader")) ) 
	showFooter=true;
if ( editFormStr != null  && minEditRoleStr != null ) {
	if ( loginHandler.getLoginStatus().equals("authenticated")) {	
		String currentSessionId = session.getId();
		String storedSessionId = loginHandler.getSessionId();
		if ( currentSessionId.equals( storedSessionId ) ) {
			String currentRemoteAddrStr = request.getRemoteAddr();
			String storedRemoteAddr = loginHandler.getLoginRemoteAddr();
			if ( currentRemoteAddrStr.equals( storedRemoteAddr ) ) {
				int minEditRole = Integer.parseInt(  minEditRoleStr );
				String authorizedRoleStr = loginHandler.getLoginRole();
				int authorizedRole = Integer.parseInt( authorizedRoleStr );
				if ( authorizedRole >= minEditRole ) { %>
					<jsp:include page="<%=editFormStr%>" flush="true">
						<jsp:param name="firstvalue" value="<%=firstValue%>" />
					</jsp:include>
<%				} else if ( showFooter ) { %>				
					<jsp:include page="footer.jsp" flush="true">
						<jsp:param name="header" value="<%=headerStr%>" />
					</jsp:include>
<%				}
			} else if ( showFooter ) { %>
				<jsp:include page="footer.jsp" flush="true">
					<jsp:param name="header" value="<%=headerStr%>" />
				</jsp:include>
<%			}
		} else if ( showFooter ) { %>
			<jsp:include page="footer.jsp" flush="true">
				<jsp:param name="header" value="<%=headerStr%>" />
			</jsp:include>
<% 	}
	} else if ( showFooter ) { %>
		<jsp:include page="footer.jsp" flush="true">
			<jsp:param name="header" value="<%=headerStr%>" />
		</jsp:include>
<% }
} else if ( showFooter ) { %>
	<jsp:include page="footer.jsp" flush="true">
		<jsp:param name="header" value="<%=headerStr%>" />
	</jsp:include>
<%
} %>
</BODY>
</HTML>					