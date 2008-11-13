%function WriteSimulationData(fName, fields, info)
function WriteSimulationData(fName, fields, info)

sdData.fields = fields;
sdData.info = info;

save(fName, 'sdData');

