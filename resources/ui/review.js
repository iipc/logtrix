function show(parent, choice) {
  $("#reporter > div").css("display", "none");
  $("#"+choice).css("display","block");
  $(".nav-link").removeClass("active");
  $(parent).addClass("active");
}

function handleFileSelect() {
  if (!window.File || !window.FileReader || !window.FileList || !window.Blob) {
    alert('The File APIs are not fully supported in this browser.');
    return;
  }

  input = document.getElementById('fileinput');
  if (!input) {
    alert("Um, couldn't find the fileinput element.");
  }
  else if (!input.files) {
    alert("This browser doesn't seem to support the `files` property of file inputs.");
  }
  else if (!input.files[0]) {
    alert("Please select a file before clicking 'Load'");
  }
  else {
    file = input.files[0];
    fr = new FileReader();
    fr.onload = receivedText;
    fr.readAsText(file);
  }
}

function receivedText() {
  let report = JSON.parse(fr.result);
  $("#file-picker").css("display","none");
  $("#reporter").css("display","block");
  $("#overview-report").css("display","block");

  $("#totalCount").text(report.totals.count.toLocaleString());
  $("#totalBytes").text(report.totals.bytes.toLocaleString());

  let codeData = [];

  Object.keys(report.statusCodes).forEach(function(code) {
    let codeValues = report.statusCodes[code];
    let entry = {};
    entry.code = code;
    entry.description = codeValues.description==null ? '' : codeValues.description;
    entry.count = codeValues.count;
    entry.duplicate = codeValues.count-codeValues.uniqueCount;
    entry.duppercent = (codeValues.count-codeValues.uniqueCount)*100/codeValues.count;
    entry.bytes = codeValues.bytes;
    entry.dupbytes = codeValues.bytes-codeValues.uniqueBytes;
    entry.dupbytesperc = (codeValues.bytes-codeValues.uniqueBytes)*100/codeValues.bytes;
    entry.rate = ((codeValues.bytes/8)/(codeValues.millis/1000))/1024;
    entry.firstTime = codeValues.firstTime;
    entry.lastTime = codeValues.lastTime;

    codeData.push(entry);
  });

console.log(codeData);

  $('#status-code-table').DataTable({
    data: codeData,
    columns: [
        { data: 'code' },
        { data: 'description' },
        {
          data: 'count',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'duplicate',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'duppercent',
          render: $.fn.dataTable.render.number( ',', '.', 2, '', '%' )
        },
        {
          data: 'bytes',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'dupbytes',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'dupbytesperc',
          render: $.fn.dataTable.render.number( ',', '.', 2, '', '%' )
        },
        {
          data: 'rate',
          render: $.fn.dataTable.render.number( ',', '.', 2)
        },
        { data: 'firstTime' },
        { data: 'lastTime' },
    ]
  });

  let mimeData = [];

  Object.keys(report.mimeTypes).forEach(function(type) {
    let typeValues = report.mimeTypes[type];
    let entry = {};
    entry.type = type;
    entry.count = typeValues.count;
    entry.duplicate = typeValues.count-typeValues.uniqueCount;
    entry.duppercent = (typeValues.count-typeValues.uniqueCount)*100/typeValues.count;
    entry.bytes = typeValues.bytes;
    entry.dupbytes = typeValues.bytes-typeValues.uniqueBytes;
    entry.dupbytesperc = (typeValues.bytes-typeValues.uniqueBytes)*100/typeValues.bytes;
    entry.rate = ((typeValues.bytes/8)/(typeValues.millis/1000))/1024;
    entry.firstTime = typeValues.firstTime;
    entry.lastTime = typeValues.lastTime;

    mimeData.push(entry);
  });

  $('#mimetype-table').DataTable({
    data: mimeData,
    columns: [
        { data: 'type' },
        {
          data: 'count',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'duplicate',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'duppercent',
          render: $.fn.dataTable.render.number( ',', '.', 2, '', '%' )
        },
        {
          data: 'bytes',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'dupbytes',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'dupbytesperc',
          render: $.fn.dataTable.render.number( ',', '.', 2, '', '%' )
        },
        {
          data: 'rate',
          render: $.fn.dataTable.render.number( ',', '.', 2)
        },
        { data: 'firstTime' },
        { data: 'lastTime' },
    ]
  });

  let domainData = [];

  Object.keys(report.registeredDomains).forEach(function(domain) {
    let values = report.registeredDomains[domain];
    let entry = {};
    entry.domain = domain;
    entry.count = values.count;
    entry.duplicate = values.count-values.uniqueCount;
    entry.duppercent = (values.count-values.uniqueCount)*100/values.count;
    entry.bytes = values.bytes;
    entry.dupbytes = values.bytes-values.uniqueBytes;
    entry.dupbytesperc = (values.bytes-values.uniqueBytes)*100/values.bytes;
    entry.rate = ((values.bytes/8)/(values.millis/1000))/1024;
    entry.firstTime = values.firstTime;
    entry.lastTime = values.lastTime;

    domainData.push(entry);
  });

  $('#domain-table').DataTable({
    data: domainData,
    columns: [
        { data: 'domain' },
        {
          data: 'count',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'duplicate',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'duppercent',
          render: $.fn.dataTable.render.number( ',', '.', 2, '', '%' )
        },
        {
          data: 'bytes',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'dupbytes',
          render: $.fn.dataTable.render.number( ',', '.', 0, '' )
        },
        {
          data: 'dupbytesperc',
          render: $.fn.dataTable.render.number( ',', '.', 2, '', '%' )
        },
        {
          data: 'rate',
          render: $.fn.dataTable.render.number( ',', '.', 2)
        },
        { data: 'firstTime' },
        { data: 'lastTime' },
    ]
  });

}
