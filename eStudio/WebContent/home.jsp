<%@page import="com.async.util.PortalUtil"%>
<%
	Boolean isValid = PortalUtil.isValidSession(request.getSession());
	if (!isValid) {
		response.sendRedirect("index.jsp");
		return;
	}
%>
	
<!-- Including Header -->
<jsp:include page='/jsp/template/header.jsp'></jsp:include>
<div class="main-container" id="main-container">
	<script type="text/javascript">
		try {
			ace.settings.check('main-container', 'fixed');
		} catch (e) {
		}
	</script>

	<!-- Main Container -->
	<div class="main-container-inner">
		<a class="menu-toggler" id="menu-toggler" href="#"> <span
			class="menu-text"></span>
		</a>

		<!-- Including Navigation -->
		<jsp:include page="/jsp/template/leftnav.jsp"></jsp:include>

		<div class="main-content">
			<!-- <div class="breadcrumbs breadcrumbs-fixed" id="breadcrumbs">
				

				<ul class="breadcrumb">
					<li><i class="icon-home home-icon"></i> <a href="#">Home</a></li>
					<li class="active">Dashboard</li>
				</ul>
				.breadcrumb
				
				<div class="nav-search" id="nav-search">
					<form class="form-search">
						<span class="input-icon"> <input type="text"
							placeholder="Search ..." class="nav-search-input"
							id="nav-search-input" autocomplete="off" /> <i
							class="icon-search nav-search-icon"></i>
						</span>
					</form>
				</div>
				#nav-search
			</div> -->
			
			<!-- Dynamic Content Goes here-->
			<div class="page-content">
				<!-- <div class="space"></div><div class="space"></div>
				 -->
				<div id="id_EmbedPage">
				
				</div>
			</div>
			<!-- /.page-content -->

		</div>
		<!-- /.main-content -->
	</div>
	<!-- /.main-container-inner -->
	</div><!-- /.main-container -->
	<script type="text/javascript">
		$(document).ready(function(){
			var URL = "embedpage.action";
			var DATA = {reqPage:"/jsp/studio/newinvoice.jsp"};
			var embedInElement = "id_EmbedPage";
	
			async.munsi.ajaxCall(URL,DATA,embedInElement);
		});
	</script>
	<!-- Including Footer -->
	<jsp:include page="/jsp/template/footer.jsp"></jsp:include>