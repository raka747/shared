clear; 
clc; 
load('allstocks.mat')


DATEFIX = '12/30/1899'; 
%%

dscodes = cell(size(allstocks)); 
for i=1:length(allstocks)
    dscodes{1,i} = allstocks(i).dscode; 
end


%%

%
% PRICEMATRIX - matrix which contains [dates,unadjusted prices stock for each column]
%

[NUMPRICEDATA, ~,RAWPRICEDATA] =  xlsread('Q2-Latest.xlsx','Sheet1'); 

% Adjust for dates
pricedates = RAWPRICEDATA(4, 3:end);
for i = 1:length(pricedates); 
    pricedates{1,i} = pricedates{1,i}+datenum(DATEFIX); 
end
UPRICES = NUMPRICEDATA(5:end, 2:end); 

PRICEMATRIX = zeros(length(pricedates), length(dscodes)+1); 

% Fill in dates; 
for i = 1:size(PRICEMATRIX, 1)
    PRICEMATRIX(i,1) = pricedates{1,i}; 
end

% Fill in price data; 
for i = 2:(length(dscodes)+1)
    PRICEMATRIX(:,i) = UPRICES(i-1,:); 
end


%%

%
% EPRICEMATRIX - matrix which contains [dates,unadjusted EURO prices stock for each column]
%

[NUMPRICEDATA, ~,RAWPRICEDATA] =  xlsread('Q2-Latest.xlsx','Sheet5'); 

% Adjust for dates
pricedates = RAWPRICEDATA(4, 3:end);
for i = 1:length(pricedates); 
    pricedates{1,i} = pricedates{1,i}+datenum(DATEFIX); 
end
UPRICES = NUMPRICEDATA(5:end, 2:end); 

EPRICEMATRIX = zeros(length(pricedates), length(dscodes)+1); 

% Fill in dates; 
for i = 1:size(EPRICEMATRIX, 1)
    EPRICEMATRIX(i,1) = pricedates{1,i}; 
end

% Fill in price data; 
for i = 2:(length(dscodes)+1)
    EPRICEMATRIX(:,i) = UPRICES(i-1,:); 
end

%% 

%
% TRIMATRIX - [DATES, TRI]
%

[NUMTRIDATA, ~, RAWTRIDATA] = xlsread('Q2-Latest.xlsx', 'Sheet2'); 
UTRI = NUMTRIDATA(5:end, 2:end); 

UTRIDD = zeros(size(UTRI,1)/2, size(UTRI,2)); 
% Remove Duplicate rows
for i=1:(size(UTRI, 1)/2)
    UTRIDD(i,:) = UTRI(2*i, :); 
end

% Assuming the dates are same in all the sheets, I have checked the number
% of columns and they are matching. 

TRIMATRIX = zeros(size(PRICEMATRIX));
TRIMATRIX(:,1) = PRICEMATRIX(:,1); 

% Fill in price data; 
for i = 2:(length(dscodes)+1)
    TRIMATRIX(:,i) = UTRIDD(i-1,:); 
end
    

%%

% 
% EVOLMATRIX - [DATES, EuroTurnover]
%
[NUMVOLDATA, ~, ~] = xlsread('Q2-Latest.xlsx', 'Sheet3'); 
UVOL = NUMVOLDATA(5:end, 2:end);


VOLMATRIX = zeros(size(PRICEMATRIX));
VOLMATRIX(:,1) = PRICEMATRIX(:,1); 

% Fill in price data; 
for i = 2:(length(dscodes)+1)
    VOLMATRIX(:,i) = UVOL(i-1,:); 
end


EVOLMATRIX = VOLMATRIX(:,2:end) .* (EPRICEMATRIX(:,2:end) ./ PRICEMATRIX(:,2:end)); 
EVOLMATRIX = [VOLMATRIX(:,1), EVOLMATRIX]; 
%%

% 
% mtbv - [dates, market to book values]
%

[NUMMTBVLDATA, ~, ~] = xlsread('Q2-Latest.xlsx', 'Sheet4'); 
UMTBV = NUMMTBVLDATA(5:end, 2:end);


MTBVMATRIX = zeros(size(PRICEMATRIX));
MTBVMATRIX(:,1) = PRICEMATRIX(:,1); 

% Fill in price data; 
for i = 2:(length(dscodes)+1)
    MTBVMATRIX(:,i) = UMTBV(i-1,:); 
end

%% 

%
% CAP - [dates, Market Cap]
%

[NUMCAPLDATA, ~, ~] = xlsread('Q2-Latest.xlsx', 'Sheet6'); 
UCAP = NUMCAPLDATA(5:end, 2:end);


CAPMATRIX = zeros(size(PRICEMATRIX));
CAPMATRIX(:,1) = PRICEMATRIX(:,1); 

% Fill in price data; 
for i = 2:(length(dscodes)+1)
    CAPMATRIX(:,i) = UCAP(i-1,:); 
end

%%

%
% AVGSPREADFINAL - [dates, averagespreads]
%

load('TickSummary.mat'); 


% Take the average across all the seven hours 
AVGSPREAD = mean(spread, 3); 

% Take only those companies which are required. 
AVGSPREADFILTERED = NaN(size(AVGSPREAD, 1), length(dscodes)); 

% dscodes contain required, dscode contains given
[temp, temp1, temp2] = intersect(dscode, dscodes);

AVGSPREADFILTERED(:, temp2) = AVGSPREAD(:, temp1); 

% Merge based on dates 
givendates = zeros(size(CAPMATRIX,1), 3); 
givendates(:,1) = CAPMATRIX(:,1); 
givendates(:,2) = month(givendates(:,1)); 
givendates(:,3) = year(givendates(:,1)); 
AVGSPREADFILTERED = [monthlist(:,1:2),AVGSPREADFILTERED ];

% Have to convert to tables to merge the data 
tAVGSPREADFILTERED = array2table(AVGSPREADFILTERED); 
tgivendates = array2table(givendates);


AVGSPREADFINAL = outerjoin(tAVGSPREADFILTERED,tgivendates, 'Type','right', 'LeftKeys',[1,2], 'RightKeys', [3,2]);
AVGSPREADFINAL = table2array(AVGSPREADFINAL); 
AVGSPREADFINAL = AVGSPREADFINAL(:,3:568);

% Back fill from 739 row.
for i = 1:738
    AVGSPREADFINAL(i, :) = AVGSPREADFINAL(739, :); 
end

AVGSPREADFINAL = [PRICEMATRIX(:,1), AVGSPREADFINAL]; 
%%

% LOAD from IBES database

[NUMIBESDATA, ~, RAWIBESDATA] = xlsread('IBES-Q4.xlsx', 'Sheet3');
IBESTickers = RAWIBESDATA(3, 2:end);
IBESTickers = cellfun(@(X) strrep(X,'@:@',''),IBESTickers,'uniformoutput',0 );

for i=1:length(IBESTickers)
    if isnan(IBESTickers{1,i})
        IBESTickers{1,i} = ''; 
    end
end

[NUMIBESDATA2] = readtable('Q4-WRDS.csv');
NUMIBESDATA2 = table2cell(NUMIBESDATA2); 
for i =1:size(NUMIBESDATA2,1)
    NUMIBESDATA2{i,5} = NUMIBESDATA2{i,3} - NUMIBESDATA2{i,4};
    NUMIBESDATA2{i,2} = datenum(num2str(NUMIBESDATA2{i,2}), 'yyyymmdd'); 
end

NUMIBESDATA2 = NUMIBESDATA2(:, [1,2,5]); 
IBESTickers = IBESTickers'; 
IBESTickers(:,2) = dscodes; 
tIBESTickers = array2table(IBESTickers); 
tNUMIBESDATA2 = array2table(NUMIBESDATA2); 

tNUMIBESDATA3 = outerjoin(tNUMIBESDATA2,tIBESTickers,'Type','left','LeftKeys',1, 'RightKeys', 1 );

tNUMIBESDATA3 = tNUMIBESDATA3(:, [2,3,5]); 
tNUMIBESDATA3 = table2array(tNUMIBESDATA3); 
tNUMIBESDATA3 = table(cell2mat(tNUMIBESDATA3(:,1)), cell2mat(tNUMIBESDATA3(:,2)),cell2mat(tNUMIBESDATA3(:,3))); 
    

tNUMIBESDATA4 = unstack(tNUMIBESDATA3,'Var2' ,'Var3');
colDSCODES = tNUMIBESDATA4.Properties.VariableNames; 
colDSCODES = colDSCODES(2:end); 
colDSCODES = cellfun(@(X) strrep(X,'x',''),colDSCODES,'uniformoutput',0  );
tNUMIBESDATA4 = table2array(tNUMIBESDATA4); 


tNUMIBESDATA5 = zeros(size(tNUMIBESDATA4,1), length(dscodes));
[~, L, R] = intersect(dscodes, colDSCODES); 
tNUMIBESDATA5(:, L) = tNUMIBESDATA4(:, R+1); 

tNUMIBESDATA5 = array2table([tNUMIBESDATA4(:,1),tNUMIBESDATA5]); 
tIBESDatesTable = array2table(PRICEMATRIX(:,1));


IBESFTable = outerjoin(tIBESDatesTable,tNUMIBESDATA5,'Type','left','LeftKeys',1, 'RightKeys', 1 );
IBESFTable = table2array(IBESFTable); 
IBESFTable = [IBESFTable(:,1), IBESFTable(:,3:end)]; 
IBESFTable(isnan(IBESFTable)) = 0 ; 


%%

[dNUMIBESDATA, ~, dRAWIBESDATA] = xlsread('IBES-Q4.xlsx', 'Sheet3');
dIBESTickers = dRAWIBESDATA(3, 2:end);
dIBESTickers = cellfun(@(X) strrep(X,'@:@',''),dIBESTickers,'uniformoutput',0 );

for i=1:length(dIBESTickers)
    if isnan(dIBESTickers{1,i})
        dIBESTickers{1,i} = ''; 
    end
end


[dNUMIBESDATA2] = readtable('Q4-WRDS.csv');
dNUMIBESDATA2 = table2cell(dNUMIBESDATA2); 
for i =1:size(dNUMIBESDATA2,1)
    dNUMIBESDATA2{i,5} = dNUMIBESDATA2{i,3} + dNUMIBESDATA2{i,4};
    dNUMIBESDATA2{i,2} = datenum(num2str(dNUMIBESDATA2{i,2}), 'yyyymmdd'); 
end

dNUMIBESDATA2 = dNUMIBESDATA2(:, [1,2,5]); 
dIBESTickers = dIBESTickers'; 
dIBESTickers(:,2) = dscodes; 
dtIBESTickers = array2table(dIBESTickers); 
dtNUMIBESDATA2 = array2table(dNUMIBESDATA2); 

dtNUMIBESDATA3 = outerjoin(dtNUMIBESDATA2,dtIBESTickers,'Type','left','LeftKeys',1, 'RightKeys', 1 );

dtNUMIBESDATA3 = dtNUMIBESDATA3(:, [2,3,5]); 
dtNUMIBESDATA3 = table2array(dtNUMIBESDATA3); 
dtNUMIBESDATA3 = table(cell2mat(dtNUMIBESDATA3(:,1)), cell2mat(dtNUMIBESDATA3(:,2)),cell2mat(dtNUMIBESDATA3(:,3))); 

dtNUMIBESDATA4 = unstack(dtNUMIBESDATA3,'Var2' ,'Var3');
dcolDSCODES = dtNUMIBESDATA4.Properties.VariableNames; 
dcolDSCODES = dcolDSCODES(2:end); 
dcolDSCODES = cellfun(@(X) strrep(X,'x',''),dcolDSCODES,'uniformoutput',0  );
dtNUMIBESDATA4 = table2array(dtNUMIBESDATA4); 


dtNUMIBESDATA5 = zeros(size(dtNUMIBESDATA4,1), length(dscodes));
[~, L, R] = intersect(dscodes, dcolDSCODES); 
dtNUMIBESDATA5(:, L) = dtNUMIBESDATA4(:, R+1); 

dtNUMIBESDATA5 = array2table([dtNUMIBESDATA4(:,1),dtNUMIBESDATA5]); 
dtIBESDatesTable = array2table(PRICEMATRIX(:,1));


dIBESFTable = outerjoin(dtIBESDatesTable,dtNUMIBESDATA5,'Type','left','LeftKeys',1, 'RightKeys', 1 );
dIBESFTable = table2array(dIBESFTable); 
dIBESFTable = [dIBESFTable(:,1), dIBESFTable(:,3:end)]; 
dIBESFTable(isnan(dIBESFTable)) = 0 ; 

%%

% if NaNs are greater than 26 then it is not active. 
NaNslimit = .1*252; 

[fiveaFilter1,fiveaFilter2, fivebFilter,fivecFilter1,fivecFilter2,fiveeFilter, fivefFilter]= deal(zeros(size(PRICEMATRIX))); 



for i=253:size(fiveaFilter1, 1)
    for j=2:size(fiveaFilter1, 2)
        NaNsum = sum(isnan(TRIMATRIX(i-252:i-1,j)));
        if NaNsum <= NaNslimit
            fiveaFilter1(i,j) = 1; 
        end    
    end    
end

for i=253:size(fiveaFilter2, 1)
    for j=2:size(fiveaFilter2, 2)
        NaNsum = sum(isnan(PRICEMATRIX(i-252:i-1,j)));
        if NaNsum <= NaNslimit
            fiveaFilter2(i,j) = 1; 
        end    
    end    
end

% fiveafilter - valid price and total return index valid for 90% of past
% 252 days. 

fiveaFilter = fiveaFilter1 .* fiveaFilter2; 

% 
for i=11:size(fivebFilter, 1)
    for j=2:size(fiveaFilter,2)
        uniqueprices = length(unique(PRICEMATRIX(i-10:i-1, j))); 
        if uniqueprices >= 2 
            fivebFilter(i,j) = 1; 
        end
    end
end

% 
volNaNslimit = .1 * 21; 

for i=22:size(fiveaFilter, 1)
    for j=2:size(fiveaFilter, 2)
        NaNsum = sum(isnan(VOLMATRIX(i-21:i-1)));
        if NaNsum <= volNaNslimit
            fivecFilter1(i,j) = 1; 
        end    
    end    
end

for i=22:size(fiveaFilter, 1)
    for j=2:size(fiveaFilter, 2)
        NaNsum = sum(isnan(TRIMATRIX(i-21:i-1)));
        if NaNsum <= volNaNslimit
            fivecFilter2(i,j) = 1; 
        end    
    end    
end

fivecFilter = fivecFilter1 .* fivecFilter2; 

fivedFilter1 = ~isnan(MTBVMATRIX); 
fivedFilter2 = ~isnan(CAPMATRIX);
fivedFilter = fivedFilter1 .* fivedFilter2; 

AVGSPREADFINAL(:,568) = month(AVGSPREADFINAL(:,1));
lastmonth = 2; 
lastmonthspread = ~isnan(AVGSPREADFINAL(23, 2:end-1)); 
for i = 24:size(AVGSPREADFINAL)
    currentmonth = AVGSPREADFINAL(i,end); 
    if currentmonth ~=lastmonth
        lastmonthspread = ~isnan(AVGSPREADFINAL(i-1, 2:end-1));
        lastmonth = AVGSPREADFINAL(i, end); 
    end
    if currentmonth ==lastmonth 
         fiveeFilter(i,2:end) = lastmonthspread; 
    end
    
end


for i=2:size(fiveeFilter,1)
    for j=2:size(fiveeFilter,2)
        fivefFilter(i,j) = (sum(dIBESFTable(1:i,j)) >0); 
    end    
end

fiveFilter = fiveaFilter .* fivebFilter .* fivecFilter .* fivedFilter .* fiveeFilter .* fivefFilter;


%%

fiveFilter(:,1) = PRICEMATRIX(:,1); 


dfiveFilter = [fiveFilter,month(fiveFilter(:,1))];
lastmonth = 2; 
lastmonthactive = (dfiveFilter(23, 2:(end-1))); 
LMACTIVE = zeros(size(fiveFilter)); 
for i = 24:size(dfiveFilter,1)
    currentmonth = dfiveFilter(i,end);
    if currentmonth ~=lastmonth
        lastmonthactive = dfiveFilter(i-1, 2:(end-1));
        lastmonth = dfiveFilter(i, end); 
    end
    if currentmonth ==lastmonth 
         LMACTIVE(i,2:end) = lastmonthactive; 
    end
    
end


% BAL1 - bid ask less than 0.01 
% BAL8 - bid ask less than 0.008

[BAL1,BAL8] = deal(zeros(size(fiveFilter))); 

lastmonth = 2; 
lastmonthspread = (AVGSPREADFINAL(23, 2:end-1)); 
for i = 24:size(AVGSPREADFINAL)
    currentmonth = AVGSPREADFINAL(i,end); 
    if currentmonth ~=lastmonth
        lastmonthspread = (AVGSPREADFINAL(i-1, 2:end-1));
        lastmonth = AVGSPREADFINAL(i, end); 
    end
    if currentmonth ==lastmonth 
         BAL1(i,2:end) = (lastmonthspread < 0.01); 
         BAL8(i,2:end) = (lastmonthspread < 0.008);
    end
    
end

sixafilter = ((LMACTIVE .* BAL1) + ((~LMACTIVE) .* BAL8 )>0);


% ADV1 - Average daily volume less than one million 
% ADV82 - Average daily volume less than 1.2 million


[ADV1,ADV82] = deal(zeros(size(fiveFilter)));

lastmonth = 2; 
lastmonthavg = nanmean(EVOLMATRIX(1:23, 2:end)); 
for i = 24:size(EVOLMATRIX)
    currentmonth = AVGSPREADFINAL(i,end); 
    if currentmonth ~=lastmonth
        lastmonthavg = nanmean(EVOLMATRIX(i-23:i-1, 2:end));
        lastmonth = AVGSPREADFINAL(i, end); 
    end
    if currentmonth ==lastmonth 
         ADV1(i,2:end) = (lastmonthavg > 1000); 
         ADV82(i,2:end) = (lastmonthavg > 1200);
    end
    
end

sixbfilter = ((LMACTIVE .* ADV1) + ((~LMACTIVE) .* ADV82 )>0);





isactivenow = fiveFilter .* sixafilter .* sixbfilter;

%%
allstocks = dscodes; 
myday = PRICEMATRIX(:,1); 
price = EPRICEMATRIX(:,2:end); 
tri = TRIMATRIX(:,2:end);
volume = EVOLMATRIX(:,2:end); 
mtbv = MTBVMATRIX(:,2:end); 
rec = IBESFTable(:, 2:end); 
tcost = AVGSPREADFINAL(:,2:end-1); 
isactivenow = isactivenow(:,2:end); 

%%
save('hw2.mat', 'allstocks', 'myday', 'price', 'tri', 'volume', 'mtbv', 'rec', 'tcost', 'isactivenow');