clc; 
clear; 

%%
filelist = {'data2/2001.xlsm', 'data2/2000.xlsm', 'data2/1997.xlsm', 'data2/1998.xlsm', 'data2/1999.xlsm'};

sheetlist = cellfun(@(X)['Sheet',X], cellfun(@num2str,num2cell(1:13),'uniformoutput',0),'uniformoutput',0 );
indiceslist = {'LBGBEL20','LDKKFXIN','LHEX25IN','LFSBF120','LXDAX100',...
    'LAMSTEOE','LAMSMKAP','LITMIB30','LITMIDEX','LIBEX35I','LOSLOOBX','LSWEDOMX','LSWISSMI'}; 
dscodes = {};
for xlfile=filelist
    for sheetfile = sheetlist
        [num, txt,raw] = xlsread(char(xlfile), char(sheetfile));
        dscodes = [dscodes,raw{3:end,7}];
    end
end
dscodes = cellfun(@num2str, dscodes, 'uniformoutput',0); 
udscodes = unique(dscodes); 

DEFAULTDATE = '01-Oct-2016'; 

%%
latestdata = cell(6,size(udscodes,2)); 
% for i=1:size(udscodes,2)
%     latestdata{1,i} = udscodes{1,i};
% end
% Read 2016 data and get corresponding name, industry, isin

LATESTDATAFILE = 'data2/2016.xlsm'; 
for j=1:size(sheetlist,2)
    sheetfile = sheetlist(1,j); 
    indexname = indiceslist(1,j); 
    [num, txt,raw] = xlsread(char(xlfile), char(sheetfile));
    cisin = raw(3:end, 5); 
    cname = raw(3:end, 6);
    cdscd = raw(3:end, 7);
    cdscd = cellfun(@num2str,cdscd,'uniformoutput',0);
    cinds = raw(3:end, 9);
    cibes = raw(3:end, 2); 
    
    [~,dL, dR] = intersect(udscodes, cdscd); 
    latestdata(1, dL) = cdscd(dR); 
    latestdata(2, dL) = cisin(dR); 
    latestdata(3, dL) = cname(dR);
    latestdata(4, dL) = cinds(dR);
    latestdata(5, dL) = cibes(dR);
    latestdata(6, dL) = indexname; 
end

%%
cisin = {}; 
cname = {}; 
cinds = {}; 
cibes = {}; 
cindx = {}; 
for i=1:size(udscodes,2)
    cisin = [cisin, struct('date' ,DEFAULTDATE, 'isin', {latestdata{2,i}}) ]; 
    cname = [cname, struct('date' ,DEFAULTDATE, 'name', {latestdata{3,i}}) ]; 
    cinds = [cinds, struct('date' ,DEFAULTDATE, 'industry', {latestdata{4,i}}) ]; 
    cibes = [cibes, struct('date' ,DEFAULTDATE, 'ibes', {latestdata{5,i}}) ]; 
    cindx = [cindx, struct('date' ,DEFAULTDATE, 'index', {latestdata{6,i}}) ];
end


%% 
allstocks = struct('dscode', udscodes, 'namelist', cname, 'industrylist', cinds, 'ibeslist', cibes, 'isinlist', cisin, 'indexlist',cindx ); 
allstcksg = allstocks; 
%%
for k=1:size(allstcksg,2)
    allstcksg(k).indexlist(1).index
end