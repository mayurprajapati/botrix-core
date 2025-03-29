window.getElementsByXpath = function (xpathToExecute, parent) {
  parent = parent || document;
  var result = [];
  var nodesSnapshot = parent.evaluate(
    xpathToExecute,
    parent,
    null,
    XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
    null
  );
  for (var i = 0; i < nodesSnapshot.snapshotLength; i++) {
    result.push(nodesSnapshot.snapshotItem(i));
  }
  return result;
};

window.getElementByXpath = function (xpathToExecute, parent) {
  parent = parent || document;
  return parent.evaluate(
    xpathToExecute,
    parent,
    null,
    XPathResult.FIRST_ORDERED_NODE_TYPE,
    null
  ).singleNodeValue;
};

window.getIframeIndexesContainingXpathElement = function (
  xpath,
  listOfIframeIndexes,
  parent
) {
  // find element
  if (getElementsByXpath(xpath, parent).length > 0) {
    return true;
  } else {
    // find iframes in parent
    const iframes = getElementsByXpath(
      "//*[name()='frame' or name()='iframe' or local-name()='frame' or local-name()='iframe']",
      parent
    );
    for (let i = 0; i < iframes.length; i++) {
      const iframe = iframes[i];
      var iframeDocument =
        iframe.contentDocument || iframe.contentWindow.document;
      if (
        getIframeIndexesContainingXpathElement(
          xpath,
          listOfIframeIndexes,
          iframeDocument
        )
      ) {
        console.log(iframeDocument);
        listOfIframeIndexes.push(i + 1);
        return true;
      }
    }
  }
  return false;
};

window.getIndexOfIframesWithXpath = function (xpath) {
  const list = [];
  return getIframeIndexesContainingXpathElement(xpath, list, document)
    ? list.reverse()
    : null;
};