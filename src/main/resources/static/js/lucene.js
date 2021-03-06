//element
var $d = window.document;
var $g = function(id){return $d.getElementById(id);};
var indexerButton = $g('indexer-button');
var searcherButton = $g('searcher-button');
var query = $g('query');
var responseTable = $g('response-table');
var responseDetail = $g('response-detail');
var indexationInformation = $g('indexation-information');

var indexerButtonAction = function() {
    indexationInformation.style["display"] = "inline";
    var xmlhttp=new XMLHttpRequest();
    xmlhttp.open("POST", 'http://localhost:8080/index');
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4) {
            if(xmlhttp.status==400){
                console.log('error');
            }else{
                indexationInformation.style["display"] = "none";
                console.log('ok')}
        }
    }
    xmlhttp.send();
};




var searchDocs = function()
{
    // remove all previous result
    while (responseTable.firstChild) {responseTable.removeChild(responseTable.firstChild);}
    while (responseDetail.firstChild) {responseDetail.removeChild(responseDetail.firstChild);}

    var tr = document.createElement('tr');
    tr.appendChild(document.createElement('th'));
    tr.appendChild(document.createElement('th'));
    tr.appendChild(document.createElement('th'));
    tr.appendChild(document.createElement('th'));
    tr.cells[0].appendChild(document.createTextNode('Date'));
    tr.cells[1].appendChild(document.createTextNode('Montant'));
    tr.cells[2].appendChild(document.createTextNode('Type'));
    tr.cells[3].appendChild(document.createTextNode('Description'));
    responseTable.appendChild(tr);

    var requestObj = new XMLHttpRequest();
    if (requestObj) {
        requestObj.open("GET", 'http://localhost:8080/search?query='+query.value);
        requestObj.onreadystatechange = function ()
        {
            if (requestObj.readyState == 4 && requestObj.status == 200) {
                var searchResponse = JSON.parse(requestObj.response);

                var totalHits = searchResponse['totalHits'];
                var searchTime = searchResponse['searchTime'];
                var totalAmount = searchResponse['totalAmount'];
                var titleResponse = totalHits + " mouvements in " + searchTime + "ms, amount : " + totalAmount + "€";
                responseDetail.appendChild(document.createTextNode(titleResponse));

                var mouvement;
                while(mouvement = searchResponse['mouvements'].pop()){
                    var tr = document.createElement('tr');

                    tr.appendChild( document.createElement('td') );
                    tr.appendChild( document.createElement('td') );
                    tr.appendChild( document.createElement('td') );
                    tr.appendChild( document.createElement('td') );

                    tr.cells[0].appendChild( document.createTextNode(mouvement.date) );
                    tr.cells[1].appendChild( document.createTextNode(mouvement.montant) );
                    tr.cells[2].appendChild( document.createTextNode(mouvement.mouvementType) );
                    tr.cells[3].appendChild( document.createTextNode(mouvement.mouvementDescription) );

                    responseTable.appendChild(tr);
                }
            } else{
                console.log(requestObj)
            }
        }
        requestObj.send();
    }
}

indexerButton.addEventListener('click',indexerButtonAction,false);
searcherButton.addEventListener('click',searchDocs,false);

