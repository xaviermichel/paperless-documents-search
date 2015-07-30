describe('DocumentSearchController', function() {

    // compatibility : http://www.w3schools.com/jsref/jsref_trim_string.asp
    if (typeof String.prototype.trim !== 'function') {
        String.prototype.trim = function() {
            return this.replace(/^\s+|\s+$/g, '');
        };
    }
    if (typeof(console) === 'undefined' || console === null) {
        console = {
            debug: function() {},
            log: function() {},
            warn: function() {},
            error: function() {}
        };
    }
    // --

    var $scope;
    beforeEach(module('edmApp'));
    beforeEach(inject(function($rootScope, $controller) {
        $scope = $rootScope.$new();
        $controller('DocumentSearchController', {
            $scope: $scope
        });
    }));

    it('should get the correct icon', function() {
        var iconName = $scope.getDocumentNodeIcon({
            id: "1ecabcaca5e51752184db6b072cd4a30",
            edmNodeType: "DOCUMENT",
            parentId: "AU616Sa7hz1onBRyPC8p",
            name: "impots_accuseReception",
            fileExtension: "pdf",
            fileContentType: "application/pdf",
            serverDocumentFilePath: "/tmp/impots_accuseReception.pdf",
            temporaryFileToken: null,
            date: 1431006862000,
            nodePath: "/tmp/impots_accuseReception.pdf"
        });
        expect(iconName).toBe('pdf');
    });

    it('should prefix by file:/// local link', function() {
        var documentPath = $scope.linkToDocument('/tmp/document.pdf');
        expect(documentPath).toBe('file:////tmp/document.pdf');
    });

    it('should not prefix by file:/// web link', function() {
        var documentPath = $scope.linkToDocument('http://plop.com/document.pdf');
        expect(documentPath).toBe('http://plop.com/document.pdf');
    });

    it('should conserve previous search when updating pattern', function() {
        $scope.searchedPattern = 'previous search';
        $scope.updateSearchPattern('new search element');
        expect($scope.searchedPattern).toBe('previous search AND new search element');
    });
});
