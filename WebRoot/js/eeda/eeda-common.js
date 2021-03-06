$(document).ready(function() {

    var eeda={};
    window.eeda=eeda;

    eeda.openMenu = function(){
        var moudleUrl = window.location.pathname.split('/')[1];
        if(moudleUrl.length>0){
            var menu_ul = $('#side-menu a[href="/'+moudleUrl+'"]').parent().parent().parent();
            menu_ul.addClass('active').find('ul').addClass('in');
        }
    }

    //dataTables builder for 1.10
    eeda.dt = function(opt){
        var option = {
            processing: opt.processing || true,
            searching: opt.searching || false,
            paging: opt.paging || true,
            serverSide: opt.serverSide || true,
        scrollX: opt.scrollX || false,
            responsive: true,
            //scrollY: opt.scrollY || '300px', //"300px",
            scrollCollapse: opt.scrollCollapse || true,
            autoWidth: opt.autoWidth || false,
            aLengthMenu: [ [10, 25, 50, 100,500,1000, -1], [10, 25, 50, 100,500,1000,"All"] ],
            language: {
                "url": "/js/lib/datatables-1.10.9/i18n/Chinese.json"
            },
            createdRow: opt.createdRow || function ( row, data, index ) {
                $(row).attr('id', data.ID);
            },
            ajax: opt.ajax || '',
            columns: opt.columns || []
        };

        var dataTable = $('#'+opt.id).DataTable(option);

        return dataTable;
    }

    var refreshUrl=function(url){
        	var state = window.history.state;
        	if(state){
        		window.history.replaceState(state, "", url);
        	}else{
        		window.history.pushState({}, "", url);
        	}
       };
       
     eeda.contactUrl=function(str,id){
    	 refreshUrl(window.location.protocol + "//" + window.location.host+window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')+1)+str+"="+id);
     };

     eeda.urlAfterSave=function(str,id){
        var http = window.location.protocol;
        var path = window.location.host+window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/')+1);
        refreshUrl( http+ "//" + path +str+"-"+id);
     };
     
     eeda.getUrlByNo= function(id, orderNo) {
     	var str = "";
     	 if(orderNo.indexOf("PS") == 0){//配送
             str = "<a href='/delivery/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("PC") == 0){//拼车
             str = "<a href='/pickupOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("HD") == 0){//回单
             str = "<a href='/returnOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("DC") == 0){//调车
             str = "<a href='/pickupOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("FC") == 0){//发车
             str = "<a href='/departOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("SGFK") == 0){//手工付款
             str = "<a href='/costMiscOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("SGSK") == 0){//手工收款
             str = "<a href='/chargeMiscOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("BX") == 0){//保险
             str = "<a href='/insuranceOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("XCBX") == 0){//行车报销
             str = "<a href='/costReimbursement/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YFBX") == 0){//应付报销
             str = "<a href='/costReimbursement/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YFSQ") == 0){//应付申请
             str = "<a href='/costPreInvoiceOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YSSQ") == 0){//应收申请
             str = "<a href='/chargePreInvoiceOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YFQR") == 0){//应付确认
             str = "<a href='/costConfirm/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YSQR") == 0){//应收确认
             str = "<a href='/chargeConfirm/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YSFP") == 0){//应收开票记录
             str = "<a href='/chargeInvoiceOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("XC") == 0){
             str = "<a href='/carsummary/edit?carSummaryId="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YSDZ") == 0){//应收对账
             str = "<a href='/chargeCheckOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YFDZ")== 0){//应付对账
             str = "<a href='/costCheckOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("ZZSQ")== 0){//转账
             str = "<a href='/transferAccountsOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("WLPJ") == 0){//往来票据
             str = "<a href='/inOutMiscOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YF") == 0){//预付
             str = "<a href='/costPrePayOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("HSD") == 0){//预付
             str = "<a href='/damageOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("YS") == 0 ){//运输单
            str = "<a href='/transferOrder/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }else if(orderNo.indexOf("TH") == 0 ){//退货单
            str = "<a href='/returnTransfer/edit?id="+id+"' target='_blank'>"+orderNo+"</a>";
         }

         return str;
     };
     
     
     
      /**
      * JS格式化
      * @param number 要格式化的数字
      * @param d [0-9]位 逗号隔开
      */

      eeda.numFormat = function(number,d) {  
    	  var numArrs = ['0','1','2','3','4','5','6','7','8','9'],
              REG_NUMBER = /^\d+(.\d+)?$/;

          d = d || 3, // 不传 是3位 千分位
          isMinus = false;
          
          if(number<0){
        	  number *= -1;
        	  isMinus = true;
          }; 

          if(isNumber(number) || isString(number) || REG_NUMBER.test(number)) {
        	  // 先转换成字符串
    	      var toString = number + '',
    	          isPoint = toString.indexOf('.'),
    	          prefix,   // 前缀
    	          suffix,   // 后缀
    	          t = '';
    	
    	      if(isPoint > 0) {
    	         prefix = toString.substring(0,isPoint);
    	         suffix = toString.substring(isPoint + 1);
    	
    	      }else if(isPoint == 0) {
    	             prefix = '';
    	             suffix = toString.substring(1);
    	      }else {
    	             prefix = toString;
    	             suffix = '';
    	      }
    	
    	      if(prefix != '') {
    	         prefixArr = prefix.split('').reverse();
    	         var isArrayIndex = isArray(d,numArrs);
    	         if(isArrayIndex > -1) {
    	        	 for(var i = 0, ilen = prefixArr.length; i < ilen; i+=1) {
    	                 t += prefixArr[i] + ((i + 1) % isArrayIndex == 0 && (i + 1) != prefixArr.length ? "," : "");
    	             }
    	             t = t.split("").reverse().join("");
    	             
    	             if(isMinus)        //判断是否为负数
      	            	 t = '-' + t;
    	             
    	             if(suffix != '') {
    	                 return t + "." + suffix;
    	             }else {
    	                 return t;
    	             }
    	         }else {
    	             return '传入的多少位不正确';
    	         }
    	      }else if(prefix == '' && suffix != ''){
                  prefix = 0;
                  return prefix + suffix;
    	      }else {
    	          return "有错误";
    	      }
         }else {
             return '传入的要格式化的数字不符合';
         }
      };
      function isArray(item,arrs) {
          for(var i = 0, ilen = arrs.length; i < ilen; i++) {
              if(item == arrs[i]) {
                  return i;
              }
          }
          return -1;
       }
       function isNumber(number) {
          return Object.prototype.toString.apply(number) === '[object Number]';
       }

       function isString(number) {
          return Object.prototype.toString.apply(number) === ['object String'];
       }
       
        eeda.getDate =  function() {
        	var d = new Date(); 
        	var year = d.getFullYear(); 
        	var month = d.getMonth()+1; 
        	var date = d.getDate(); 
        	var day = d.getDay(); 
        	var hours = d.getHours(); 
        	var minutes = d.getMinutes(); 
        	var seconds = d.getSeconds(); 
        	var ms = d.getMilliseconds(); 
        	var curDateTime= year;
        	if(month>9)
        		curDateTime = curDateTime +"-"+month;
        	else
        		curDateTime = curDateTime +"-0"+month;
        	if(date>9)
        		curDateTime = curDateTime +"-"+date;
        	else
        		curDateTime = curDateTime +"-0"+date;
        	if(hours>9)
        		curDateTime = curDateTime +" "+hours;
        	else
        		curDateTime = curDateTime +"0"+hours;
        	if(minutes>9)
        		curDateTime = curDateTime +":"+minutes;
        	else
        		curDateTime = curDateTime +":0"+minutes;
        	if(seconds>9)
        		curDateTime = curDateTime +":"+seconds;
        	else
        		curDateTime = curDateTime +":0"+seconds;
        	return curDateTime; 
    	}
     


     window.onunload=function(){
        //页面刷新时调用，这里需要判断是否当前单据是否有更新，提示用户先保存
    	//暂时不处理 
     };

      eeda.buildTableDetail=function(table_id, deletedTableIds){
          var item_table_rows = $("#"+table_id+" tr");
            var items_array=[];
            for(var index=0; index<item_table_rows.length; index++){
                if(index==0)
                    continue;

                var row = item_table_rows[index];
                var empty = $(row).find('.dataTables_empty').text();
                if(empty)
                  continue;
                
                var id = $(row).attr('id');
                if(!id){
                    id='';
                }
                
                var item={}
                item.id = id;
                for(var i = 1; i < row.childNodes.length; i++){
                  var name = $(row.childNodes[i]).find('input,select').attr('name');
                  var value = $(row.childNodes[i]).find('input,select').val();
                  if(name){
                    item[name] = value;
                  }
                }
                item.action = id.length > 0?'UPDATE':'CREATE';
                items_array.push(item);
            }

            //add deleted items
            for(var index=0; index<deletedTableIds.length; index++){
                var id = deletedTableIds[index];
                var item={
                    id: id,
                    action: 'DELETE'
                };
                items_array.push(item);
            }
            deletedTableIds = [];
            return items_array;
        };
        
        
        
      //dataTable里的下拉列表，查询参数为input,url,添加的参数para,下拉显示的数据库字段
    eeda.bindTableField = function(table_id, el_name,url,para) {
 		  var tableFieldList = $('#table_input_field_list');

       //处理中文输入法, 没完成前不触发查询
       var cpLock = false;
       $('#'+table_id+' input[name='+el_name+'_input]').on('compositionstart', function () {
           cpLock = true;
       }).on('compositionend', function () {
           cpLock = false;
       });

       $('#'+table_id+' input[name='+el_name+'_input]').on('keyup click', function(event){

		    var me = this;
		    var inputField = $(this);
		    var hiddenField = $(this).parent().find('input[name='+el_name+']');
		    var inputStr = inputField.val();

             if(cpLock)
                 return;

             if (event.keyCode == 40) {
                 tableFieldList.find('li').first().focus();
                 return false;
             }

     			  $.get(url, {input:inputStr,para:para}, function(data){
       				  if(inputStr!=inputField.val()){//查询条件与当前输入值不相等，返回
       					  return;
       				  }
       				  tableFieldList.empty();

       				  for(var i = 0; i < data.length; i++)
       					  tableFieldList.append("<li tabindex='"+i+"'><a class='fromLocationItem' dataId='"+data[i].ID
       							  +"' item_name='"+data[i].ITEM_NAME+"' serial_no='"+data[i].SERIAL_NO+"' item_no='"+data[i].ITEM_NO+"'>"+data[i].ITEM_NAME+"\t"+data[i].SERIAL_NO+"</a></li>");
       				  if(data.length==0){
       					tableFieldList.append("<li tabindex='"+i+"' style='text-align:center'>无您要找的产品，请自行添加维护</li>");
       				  }
       					  
       				  tableFieldList.css({ 
       					  left:$(me).offset().left+"px", 
                   top:$(me).offset().top+28+"px" 
                 });
                 tableFieldList.show();
                 eeda._inputField = inputField;
                 eeda._hiddenField = hiddenField;
       	    },'json');
     		  });

     	   tableFieldList.on('click', '.fromLocationItem', function(e){
     			  var inputField = eeda._inputField;
     			  var hiddenField = eeda._hiddenField;
     			  inputField.val($(this).attr('item_name'));//名字
     			  tableFieldList.hide();
     			  var dataId = $(this).attr('dataId');
     			  hiddenField.val(dataId);//id
 		    });

           tableFieldList.on('keydown', 'li', function(e){
             if (e.keyCode == 13) {//enter
               var inputField = eeda._inputField;
               var hiddenField = eeda._hiddenField;
               inputField.val($(this).text());//名字
               tableFieldList.hide();
               var dataId = $(this).find('a').attr('dataId');
               hiddenField.val(dataId);//id

               var td = inputField.parent().parent();
               var row = td.parent();
               var colCount = row.find('td').length;

               var nextTdInput, nextTd=td;
               var index = 0;
               while(!nextTdInput && index<colCount){
                   nextTd = nextTd.next();
                   index = nextTd.index();
                   nextTdInput = nextTd.find('input:last');
                   if(nextTdInput && !nextTdInput.prop('disabled')){
                       nextTdInput.focus();
                       break;
                   }else{
                       nextTdInput=null;
                   }
               }
             }
           });

     		  // 1 没选中客户，焦点离开，隐藏列表
     		  $(document).on('click', function(event){
               if (tableFieldList.is(':visible') ){
                   var clickedEl = $(this);
                   var hiddenField = eeda._hiddenField;
                   var inputField = eeda._inputField;
                   if ($(this).find('a').val().trim().length ==0 && hiddenField=='') {
                     hiddenField.val('');
                     inputField.val('');
                   };
                   tableFieldList.hide();
               }
     		  });

     		  // 2 当用户只点击了滚动条，没选客户，再点击页面别的地方时，隐藏列表
     		  tableFieldList.on('mousedown', function(){
     			  return false;//阻止事件回流，不触发 $('#spMessage').on('blur'
     		  });

           tableFieldList.on('focus', 'li', function() {
               $this = $(this);
               $this.addClass('active').siblings().removeClass();
           }).on('keydown', 'li', function(e) {
               $this = $(this);
               if (e.keyCode == 40) {
                   $this.next().focus();
                   return false;
               } else if (e.keyCode == 38) {
                   $this.prev().focus();
                   return false;
               }
           }).find('li').first().focus();
     	  };
});