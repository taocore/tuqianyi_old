<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
	var selectedItems = new Array();
</script>

<div>
	<div class="toolbar">
		<div class="left">
			<form id="search-form">
			<select name="filter.saleStatus">
				<option value="0">出售中</option>
				<option value="1">库存中</option>
			</select>
			<select name="filter.sellerCids">
				<option value=''>所有分类</option>
				<s:iterator value="categories">
					<option value='<s:property value="cid"/>' parent='<s:property value="parentCid"/>'>
						<s:if test="%{parentCid != 0}">└</s:if>
						<s:property value="name" />
					</option>
				</s:iterator>
			</select>
			<input id="keyword" type="text" name="filter.keyWord" value="关键字" style="margin:0.5em 0;"></input>
			<button id='search' style="margin:0.5em 0;">查找</button>
			</form>
		</div>
		<div class="right">
			<span class="selection" title='可跨页及跨查找结果选择'></span>
			<a href="#" id="clear-selection">清除重选</a>
			<button id='batch-promote'>批量加标签</button>
			<button id='batch-cancel'>批量恢复</button>
		</div>
		<div class="clear"></div>
	</div>
	<div id="items">
		<s:include value="items.jsp"/>
	</div>
	<div id="pager" class="pager span-16 separator"></div>
</div>

<div id="label-dialog" title="加标签">
	<s:include value="merge.jsp"/>
</div>

<div id="processing-dialog" title="正在处理...">
	<div class="info">
		<img src="images/processing.gif" alt="processing" /><span>正在处理，请稍等...</span>
	</div>
</div>

<script type="text/javascript">
	$(".selection").tooltip();
	$("button").button();
	$("#clear-selection").click(clearSelection);
	$("#label-dialog").dialog({
		autoOpen: false,
		modal: true,
		width: 800,
		show: "blind"
	});
	$("#processing-dialog").dialog({
		autoOpen: false,
		modal: true,
		resizable: false,
		open: function(event, ui) { 
			$(".ui-dialog-titlebar-close").hide(); 
		}
	});
	
	$("#batch-promote").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		var $dialog = $("#label-dialog");
		$dialog.dialog("option", "buttons", {
			确定: function() {
				if (!validatePromotionForm())
				{
					return false;
				}
				showProcessingDialog();
				var url = "add_promotion.action";
				var q = getPromotionParameter() + "&promotionAddRequest.numIids=" + selectedItems.join();
				$.ajax({
					url: url,
					data: q,
					type: 'POST',
					success: function(data) {
						hideProcessingDialog();
						$dialog.dialog( "close" );
						reload(clearSelection);
					}
				});
				return false;
			},
			取消: function() {
				$(this).dialog( "close" );
			}
		});
		$dialog.dialog("open");
		return false;
	});
	
	$("#batch-cancel").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		showProcessingDialog();
		var promotionIds = selectedItems.join();
		var url = "batch_delete_promotions.action?numIids=" + promotionIds;
		$.ajax({
			url: url,
			success: function(data) {
				hideProcessingDialog();
				reload(clearSelection);
			}
		});
		return false;
	});
	
	$("#batch-update").click(function(){
		if (selectedItems.length == 0)
		{
			alert("未选中宝贝。");
			return false;
		}
		var $dialog = $("#label-dialog");
		$dialog.dialog("option", "buttons", {
			确定: function() {
				if (!validatePromotionForm())
				{
					return false;
				}
				showProcessingDialog();
				var url = "update_promotion.action";
				var q = getPromotionParameter() + "&promotionAddRequest.numIids=" + selectedItems.join();
				$.ajax({
					url: url,
					data: q,
					type: 'POST',
					success: function(data) {
						hideProcessingDialog();
						$dialog.dialog( "close" );
						reload(clearSelection);
					}
				});
				return false;
			},
			取消: function() {
				$(this).dialog( "close" );
			}
		});
		$dialog.dialog("open");
		return false;
	});
	
	function checkSelection()
	{
		$("tbody input.selector").each(function()
		{
			var num_iid = $(this).closest("tr").attr("num_iid");
			var found = $.grep(selectedItems, function(a){
				return a == num_iid;
			}, false);
			$(this).attr("checked", (found.length > 0));
			$(this).trigger('change');
		});
	}
	
	function clearSelection()
	{
		selectedItems = new Array();
		checkSelection();
	}
	
	var $keyword = $("#keyword");
	$keyword.css('color', '#666');
	$keyword.focus(function() {
		if ($(this).val() == '关键字') {
			$(this).css('color', 'black');
			$(this).val('');
		}
	});
	$keyword.blur(function() {
		if ($(this).val() == '') {
			$(this).val('关键字');
			$(this).css('color', '#666');
		}
	});
	
	$("#search").click(function(){
		var q = getFilterParameters();
		$("#items").data('q', q);
		loadPage(1);
		return false;
	});
	
	$("#search-form").submit(function()
	{
		return false;
	});
	
	function getFilterParameters()
	{
		var $form = $("#search-form");
		var saleStatus = $("select[name='filter.saleStatus']", $form).val();
		var cids = null;
		var c = $("select[name='filter.sellerCids']", $form);
		var cid = c.val();
		if (cid)
		{
			cids = [cid];
			$("option[parent='" + cid + "']", $form).each(function(){
				cids.push($(this).val());
			});
		}
		var keyword = $("input[name='filter.keyWord']", $form).val();
		if (keyword == '关键字')
		{
			keyword = null;
		}
		var q = 'filter.saleStatus=' + saleStatus;
		if (cids)
		{
			q += '&filter.sellerCids=' + cids;
		}
		if (keyword)
		{
			q += '&filter.keyWord=' + keyword;
		}
		return q;
	}
	
	$("#pager").pager({ pagenumber: 1, pagecount: <s:property value="pagingItems.totalPages"/>, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
	
	function loadPage(number) {
		loadPage(number, null);
	}
	
	function loadPage(number, callback) {
		$("#items").html("<img src='images/loading.gif'/>");
        var limit = <s:property value="pagingItems.option.limit"/>;
        var offset = (number - 1) * limit;
        var q = $("#items").data('q') + "&option.limit=" + limit + "&option.offset=" + offset;
        var url = "items.action";
		$.ajax({
			url: url,
			data: q,
			type: 'POST',
			success: function(data) {
				$("#items").html(data);
				checkSelection();
				var pageCount = parseInt($("#items-table").attr("pages"));
				$("#pager").pager({ pagenumber: number, pagecount: pageCount, buttonClickCallback: loadPage, firstLabel: "首页", prevLabel: "前一页", nextLabel: "下一页", lastLabel: "末页" });
				if (callback)
				{
					callback();
				}
			}
		});
    }
	
	function reload()
	{
		reload(null);
	}
	
	function reload(callback)
	{
		var i = parseInt($("#items-table").attr("pageIndex"));
		loadPage(i+1, callback);
	}
	
	function getPromotionParameter()
	{
		var form = $("#promotion-form");
		var discountType = $("input:radio[name='discount_type']:checked", form).val();
		var discountValue = $("input[name='discount_value']", form).val();
		var startDate = $("input[name='start_date']", form).val();
		var endDate = $("input[name='end_date']", form).val();
		var title = $("input[name='title']", form).val();
		var desc = $("input[name='description']", form).val();
		var q = "promotionAddRequest.tagId=1"
			+ "&promotionAddRequest.discountType=" + discountType
			+ "&promotionAddRequest.discountValue=" + discountValue
			+ "&promotionAddRequest.startDate=" + startDate
			+ "&promotionAddRequest.endDate=" + endDate
			+ "&promotionAddRequest.promotionTitle=" + title
			+ "&promotionAddRequest.promotionDesc=" + desc;
		return q;
	}
	
	function showProcessingDialog()
	{
		$("#processing-dialog").dialog("open");
	}
	
	function hideProcessingDialog()
	{
		$("#processing-dialog").dialog("close");
	}
</script>