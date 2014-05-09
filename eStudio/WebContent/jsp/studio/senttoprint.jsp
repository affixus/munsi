
<%@page import="com.async.util.Constants.DBCollectionEnum"%>
<%@page import="com.async.util.CommonUtil"%>
<%@page import="com.async.util.Constants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

						<div class="page-header">
							
							<h1>Order Book
								<small>
									<i class="icon-double-angle-right"></i>
									List of orders that are sent to print
								</small>
							</h1>
						</div><!-- /.page-header -->

						<div class="row">
							<div class="col-xs-12">
								<!-- PAGE CONTENT BEGINS -->
								
								<table id="grid-table-data"></table>

								<div id="grid-pager-data"></div>
							
								
								<!-- Dialog Scheme -->
								<div id="dialog-scheme" class="hide">
									<table id="grid-table-scheme"></table>
								</div>
							</div><!-- /.col -->
						</div><!-- /.row -->

	<script type="text/javascript">
	$(document).ready(function () {
        var myData = [
				{'id':1,'customerName':'Tom Smith','mobileNo':5546552142,'totalAmount':500,'balanceAmount':200,'orderDate':"15/4/2010",'estimatedDate':"25/4/2010",'status':"Sent to Print"},
				{'id':2,'customerName':'Ken Smith','mobileNo':5462541238,'totalAmount':500,'balanceAmount':200,'orderDate':"15/4/2010",'estimatedDate':"5/8/2010",'status':"Sent to Print"},
				{'id':3,'customerName':'D Raxon','mobileNo':9898525421,'totalAmount':500,'balanceAmount':200,'orderDate':"15/4/2010",'estimatedDate':"2/5/2010",'status':"Sent to Print"}
            ],
            myGrid = $("#grid-table-data");

        myGrid.jqGrid({

			/* url: "${pageContext.request.contextPath}/invoiceaction.do?op=view_all",
			mtype: "POST",
			loadonce: true,
			gridview: true,
			datatype: "json",
			 */
            datatype:'local',
            data: myData,
			colNames:['id','Customer Name','Mobile No.','Total Amount','Balance Amount','Order Date','Estimated Date','Status',' '],
			colModel:[
				{name:'id',index:'id', width:60, sorttype:"int", editrules:{required:false, addhidden:true}, editable: false, hidden:true},
				{name:'customerName',index:'customerName', width:170,editable: false},
				{name:'mobileNo',index:'mobileNo', width:150,editable: false},
				{name:'totalAmount',index:'totalAmount', width:100, editable: false},
				{name:'balanceAmount',index:'balanceAmount',width:100, editable: false},
				{name:'orderDate',index:'orderDate',width:100,sorttype:'date', searchoptions: {sopt: ['eq'],
                    dataInit : function (elem) {
                        $(elem).datepicker({ format:'dd-M-yyyy' ,changeYear: true, changeMonth: true, showButtonPanel: true, autoclose: true}) .on('changeDate', function(ev){
							
                        		if (this.id.substr(0, 3) === "gs_") {
                                    setTimeout(function(){
                                    	myGrid[0].triggerToolbar();
                                    }, 50);
                                } else {
                                    $(this).trigger('change');
                                }
                        });
                        
                    }},formatter:'date', formatoptions: {newformat:'d-M-Y'}, datefmt: 'd-M-Y',unformat: pickDate},
				{name:'estimatedDate',index:'estimatedDate',width:130, formatter:'date',  searchoptions: {sopt: ['eq'],
                    dataInit : function (elem) {
                        $(elem).datepicker({ format:'dd-M-yyyy' ,changeYear: true, changeMonth: true, showButtonPanel: true, autoclose: true}) .on('changeDate', function(ev){
							
                        		if (this.id.substr(0, 3) === "gs_") {
                                    setTimeout(function(){
                                    	myGrid[0].triggerToolbar();
                                    }, 50);
                                } else {
                                    $(this).trigger('change');
                                }
                        });
                        
                    }},formatoptions: {newformat:'d-M-Y'}, datefmt: 'd-M-Y',unformat: pickDate},
				{name:'status',index:'status',width:150, editable: true, search : false, formatter:'select',edittype:"select",stype:'select',editoptions:{value:"Raw Data:Raw Data;Final Data:Final Data;Sent to Print:Sent to Print;Received from Print:Received from Print;Delivered to Customer:Delivered to Customer"}, searchoptions:{value:":;Raw Data:Raw Data;Final Data:Final Data;Sent to Print:Sent to Print;Received from Print:Received from Print;Delivered to Customer:Delivered to Customer"}},
				{ name: 'act', index: 'act', frozen : true,width:70, search:false, align: 'center', sortable: false, formatter: 'actions',
                    formatoptions: {editbutton:true,delbutton:false,
                        keys: false
                    }
                }
			], 
			loadComplete : function() {
				var table = this;
				setTimeout(function(){	
					updatePagerIcons(table);
					enableTooltips(table);
				}, 0);
				var editparameters = {
                		"keys" : false,
                		"oneditfunc" : null,
                		"successfunc" : null,
                		"url" : null,
                	    "extraparam" : {},
                		"aftersavefunc" : null,
                		"errorfunc": null,
                		"afterrestorefunc" : null,
                		"restoreAfterError" : true,
                		"mtype" : "POST"
                	};
				var iCol = getColumnIndexByName(myGrid, 'act');
                $(this).find(">tbody>tr.jqgrow>td:nth-child(" + (iCol + 1) + ")")
                    .each(function() {
                        $("<div>", {
                            title: "Show Details",
                            mouseover: function() {
                                $(this).addClass('ui-state-hover');
                            },
                            mouseout: function() {
                                $(this).removeClass('ui-state-hover');
                            },
                            click: function(e) {
                                alert("'Detail' button is clicked in the rowis="+
                                    $(e.target).closest("tr.jqgrow").attr("id") +" !");
                            }
                        }
                      ).css({"margin-right": "5px", float: "left", cursor: "pointer"})
                       .addClass("ui-pg-div ui-inline-custom")
                       .append('<span class="icon-eye-open bigger-120" style="color:black"></span>')
                       .prependTo($(this).children("div"));
                       
                        
                });
			},
			gridComplete : function() {
				myGrid.find(".ui-jqgrid-bdiv").css({'overflow-x':'hidden'});
			},
			rowNum:10,
            rowList:[10,20,30],
            pager: '#grid-pager-data',
            gridview:true,
            ignoreCase:true,
            rownumbers:true,
            sortname: 'invdate',
            scrollOffset: 18,
			viewrecords: true,
            autowidth:true,
            sortorder: 'desc',
            height: '366'
        });
        myGrid.jqGrid('filterToolbar', {stringResult: true, searchOnEnter: false, defaultSearch : "cn"});
        myGrid.jqGrid('setFrozenColumns');
        
        function getColumnIndexByName(grid, columnName) {
            var cm = grid.jqGrid('getGridParam', 'colModel'), i, l = cm.length;
            for (i = 0; i < l; i++) {
                if (cm[i].name === columnName) {
                    return i; // return the index
                }
            }
            return -1;
        };
        
      //enable datepicker
    	function pickDate( cellvalue, options, cell ) {
    		setTimeout(function(){
    			$(cell) .find('input[type=text]')
    					.datepicker({format:'dd-mm-yyyy' , autoclose:true}); 
    		}, 0);
    	}
    	//replace icons with FontAwesome icons like above
		function updatePagerIcons(table) {
			var replacement = 
			{
				'ui-icon-seek-first' : 'icon-double-angle-left bigger-140',
				'ui-icon-seek-prev' : 'icon-angle-left bigger-140',
				'ui-icon-seek-next' : 'icon-angle-right bigger-140',
				'ui-icon-seek-end' : 'icon-double-angle-right bigger-140'
			};
			$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
				var icon = $(this);
				var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
				
				if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
			});
		}
	
		function enableTooltips(table) {
			$('.navtable .ui-pg-button').tooltip({container:'body'});
			$(table).find('.ui-pg-div').tooltip({container:'body'});
		}
	
    });
				
		</script>