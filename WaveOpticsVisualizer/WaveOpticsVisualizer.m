function varargout = WaveOpticsVisualizer(varargin)
% WAVEOPTICSVISUALIZER M-file for WaveOpticsVisualizer.fig
%      WAVEOPTICSVISUALIZER, by itself, creates a new WAVEOPTICSVISUALIZER or raises the existing
%      singleton*.
%
%      H = WAVEOPTICSVISUALIZER returns the handle to a new WAVEOPTICSVISUALIZER or the handle to
%      the existing singleton*.
%
%      WAVEOPTICSVISUALIZER('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in WAVEOPTICSVISUALIZER.M with the given input arguments.
%
%      WAVEOPTICSVISUALIZER('Property','Value',...) creates a new WAVEOPTICSVISUALIZER or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before SimPlotter_OpeningFunction gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to WaveOpticsVisualizer_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help WaveOpticsVisualizer

% Last Modified by GUIDE v2.5 13-Nov-2008 12:36:33

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @WaveOpticsVisualizer_OpeningFcn, ...
                   'gui_OutputFcn',  @WaveOpticsVisualizer_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before WaveOpticsVisualizer is made visible.
function WaveOpticsVisualizer_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to WaveOpticsVisualizer (see VARARGIN)

% Choose default command line output for WaveOpticsVisualizer
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

%Set parameters.
handles.guiDir = pwd;

%Set the toolbar.
set(hObject,'toolbar','figure');
guidata(hObject, handles);



% UIWAIT makes WaveOpticsVisualizer wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = WaveOpticsVisualizer_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;

% --- Select a graph to plot.
function SelectGraph(index, handles)

if (strcmpi(handles.sdData.fields{index}.opName, 'ZernikeDecompose'))
  plot(handles.sdData.fields{index}.List);
else
  PlotField(handles.sdData.fields{index});
end

set(handles.FunctionText,'String', handles.sdData.fields{index}.opName);
set(handles.ParametersText,'String', ...
    strcat(['(' handles.sdData.fields{index}.opParam ')']));




% --- Executes on selection change in FieldsListBox.
function FieldsListBox_Callback(hObject, eventdata, handles)
% hObject    handle to FieldsListBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = get(hObject,'String') returns FieldsListBox contents as cell array
%        contents{get(hObject,'Value')} returns selected item from FieldsListBox

%cla(handles.axes1,'reset')
index=get(hObject,'Value');
SelectGraph(index, handles);
guidata(hObject, handles);



% --- Executes during object creation, after setting all properties.
function FieldsListBox_CreateFcn(hObject, eventdata, handles)
% hObject    handle to FieldsListBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function edit1_Callback(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double


% --- Executes during object creation, after setting all properties.
function edit1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --------------------------------------------------------------------
function Untitled_1_Callback(hObject, eventdata, handles)
% hObject    handle to Untitled_1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function File_Callback(hObject, eventdata, handles)
% hObject    handle to File (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function Open_1_Callback(hObject, eventdata, handles)
% hObject    handle to Open_1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

[fname,fpath] = uigetfile({'*.sdt','Sim data file (*.sdt)'}, ...
    'Select Sim SDT file',[pwd, filesep, '', filesep]);
if fname == 0 return; end

fullPath = strcat([fpath fname]);
load(fullPath, '-mat', 'sdData');

handles.sdData = sdData;

%Break up.
parts=regexp(sdData.info, '([^\n]*)\n', 'match');
set(handles.InformationListBox, 'String', parts);
set(handles.InformationListBox, 'Value', 1);

%Fields.
for k=1:length(sdData.fields)
    if (isfield(sdData.fields{k}, 'name')) 
        fieldList{k}=sdData.fields{k}.name;
    else
        fieldList{k}=strcat(['F' num2str(k) sdData.fields{k}.opName]);
    end
end
set(handles.FieldsListBox, 'String', fieldList);
set(handles.FieldsListBox, 'Value', 1);

set(handles.figure1, 'Name', ...
    sprintf('WaveOpticsVisualizer - %s', fullPath))

SelectGraph(1, handles);
guidata(hObject,handles);







% --- Executes on selection change in InformationListBox.
function InformationListBox_Callback(hObject, eventdata, handles)
% hObject    handle to InformationListBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = get(hObject,'String') returns InformationListBox contents as cell array
%        contents{get(hObject,'Value')} returns selected item from InformationListBox


% --- Executes during object creation, after setting all properties.
function InformationListBox_CreateFcn(hObject, eventdata, handles)
% hObject    handle to InformationListBox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



