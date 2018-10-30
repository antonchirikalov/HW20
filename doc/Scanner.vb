Option Explicit
'**************************************************************************
'
'Klasse zur Anbindung eines Scanners
'
'Autor:     Dipl. Ing. Joachim Schipper
'Erstellt:  July 2002
'
'Aufgabe:
'
'   - Erstellen einer Verbindung zu einem Handscanner
'   - Entgegennehmen von Scanncodes
'   - Beenden der Verbindung
'
'   Methoden:
'   gbINIT
'       --> Herstellen der Verbindung
'       (Festlegen der Schnittstellenparameter, der Auswerteart und
'       des Endezeichens)
'   gbDeINIT
'       --> Beenden der Verbindung
'
'   benötigte CallBack-Routine:
'   gHW_Scanner_Spruch
'       --> Muß public in einem Modul hinterlegt sein
'
'**************************************************************************

Private mfrm As frmCOM
Private WithEvents mTMR As Timer
Private WithEvents mCOMM As MSComm
Private mlCommport As Integer
Private mb7BitAcII As Boolean
Private msEndeZeichen As String
Private msName As String


Public Property Get Name() As Variant
    Name = msName
End Property

Public Function gbINIT(Port As Integer, _
                      Settings As String, _
                      Handshake As HandshakeConstants, _
                      i7BitAscII As Integer, sEndeZeichen As String, Optional sName As String = "SCA") As Boolean
On Error GoTo FEHLER
'Öffnen der Schnittstelle
msName = sName

    Select Case sEndeZeichen
        Case "CR"
            msEndeZeichen = vbCr
        Case "CRLF"
            msEndeZeichen = vbCrLf
    End Select
    With mCOMM
        If i7BitAscII = 1 Then mb7BitAcII = True
                
        .Settings = Settings
        .Handshaking = Handshake
        .CommPort = Port
        mlCommport = Port
        .InputLen = 0
        .InputMode = comInputModeText
        .RTSEnable = True
        .RThreshold = 1
        .PortOpen = True
    
    End With

    gbINIT = True

Exit Function
FEHLER:
    MsgBox Err.Description, vbCritical, "hwSCANNER - gbINIT"
End Function

Public Sub gDeinit()
    mTMR.Enabled = False
    mTMR.Interval = 0
    If mCOMM.PortOpen = True Then
        mCOMM.PortOpen = False
    End If
End Sub

Private Sub Class_Initialize()
    Set mfrm = New frmCOM
    Load mfrm
    Set mTMR = mfrm.tmr
    Set mCOMM = mfrm.com
End Sub

Private Sub Class_Terminate()
    Set mTMR = Nothing
    Set mCOMM = Nothing
    Unload mfrm
    Set mfrm = Nothing
End Sub

Public Sub gsFunction(sFunction As String)
Dim sbuf As String
    If mCOMM.PortOpen = True Then
        Select Case sFunction
            Case "NR" 'NoRead Signal
'                mCOMM.Output = Chr(27) & "[8q" & Chr(27) & "[5q" & Chr(27) & "[4q" & Chr(27) & "[5q" & Chr(27) & "[9q" & vbCr
                 sbuf = Chr(ETX)  'Chr(18) & Chr(27) & "[6q" & Chr(13) & Chr(10)
            Case "GR" 'GoodRead Signal
                 sbuf = Chr(STX)  ' Chr(18) & Chr(27) & "[8q" & Chr(13) & Chr(10)
            Case Else
                sbuf = sFunction
        
        End Select
        Call frmCOMLOG.gADD(mCOMM.CommPort, "S-->" & gsParse(sbuf))
        
        mCOMM.Output = sbuf
        
    End If
End Sub


'Private Sub mCOMM_OnComm()
'Dim ev As Long
'Dim sbuf As String
'Dim sEmpu As String
'Dim dTimeout As Double
'Dim dtStopTime As Date
'
'    ev = mCOMM.CommEvent
'    If ev = comEvReceive Then
'        'Es sind Daten da...
'        dTimeout = 0.01 / 60 / 60 / 24
'        dtStopTime = Now + dTimeout
'        Do
'            sEmpu = mCOMM.Input
'            Debug.Print "------[" & sEmpu & "]"
'            If Len(sEmpu) > 0 Then Call frmCOMLOG.gADD(mCOMM.CommPort, "E<--" & sEmpu)
'            sbuf = sbuf & mStrTo7BitAcii(sEmpu)
'            sEmpu = ""
'            If InStr(1, sbuf, msEndeZeichen) > 0 And Len(msEndeZeichen) > 0 Then Exit Do
'            If Now > dtStopTime Then gSchreibeLog "Timeout beim Scannen", priFEHLER: Exit Do
'        Loop
'        If Len(sbuf) Then
'            Call gHW_Scanner_Spruch(mlCommport, sbuf)
'            Call frmCOMLOG.gADD(mCOMM.CommPort, "V<--" & sbuf)
'        End If
'    End If
'
'End Sub

Private Sub mCOMM_OnComm()
Static sBuffer As String
Dim ev As Long

    ev = mCOMM.CommEvent
    If ev = comEvReceive Then
        sBuffer = sBuffer & mCOMM.Input
        If InStr(1, sBuffer, msEndeZeichen) > 0 Then
            Call frmCOMLOG.gADD(mCOMM.CommPort, "V<--" & sBuffer)
           Call gHW_Scanner_Spruch(mlCommport, sBuffer)
            sBuffer = ""
        End If
    End If

End Sub


Private Function mStrTo7BitAcii(sText As String) As String
Dim i As Long
Dim sZeichen As String
Dim stmp As String
    For i = 1 To Len(sText)
        sZeichen = Mid(sText, i, 1)
        If Asc(sZeichen) > 127 Then
            sZeichen = Chr(Asc(sZeichen) Xor 128)
        End If
        stmp = stmp & sZeichen
    Next i
    mStrTo7BitAcii = stmp
End Function



Private Function mCommEventText(lEvent As Long) As String
Dim sErr As String
Dim sMsg As String
  ' Fehler
  Select Case lEvent
      Case comBreak   ' Es wurde ein Anhaltesignal empfangen.
        sErr = "Es wurde ein Anhaltesignal empfangen."
      Case comFrame   ' Fehler im Übertragungsraster (Framing Error)
        sErr = "Fehler im Übertragungsraster (Framing Error)"
      Case comOverrun   ' Datenverlust
        sErr = "Datenverlust"
      Case comRxOver   ' Überlauf des Empfangspuffers
        sErr = "Überlauf des Empfangspuffers"
      Case comRxParity   ' Paritätsfehler
        sErr = "Paritätsfehler"
      Case comTxFull   ' Sendepuffer voll
        sErr = "Sendepuffer voll"
      Case comDCB   ' Unerwarteter Fehler beim Abrufen des DCB
        sErr = "Unerwarteter Fehler beim Abrufen des DCB"
   ' Ereignisse
      Case comEvCD   ' Pegeländerung auf DCD
        sMsg = "Pegeländerung auf DCD"
      Case comEvCTS   ' Pegeländerung auf CTS
        sMsg = "Pegeländerung auf CTS"
      Case comEvDSR   ' Pegeländerung auf DSR
        sMsg = "Pegeländerung auf DSR"
      Case comEvRing   ' Pegeländerung auf RI
        sMsg = "Pegeländerung auf RI (Ring Indicator)"
      Case comEvReceive   ' Anzahl empfangener Zeichen gleich RThreshold
        sMsg = "Anzahl empfangener Zeichen gleich RThreshold"
      Case comEvSend   ' Im Sendepuffer befinden sich SThreshold Zeichen
        sMsg = "Im Sendepuffer befinden sich SThreshold Zeichen"
      Case comEvEOF   ' Im Eingabestrom wurde ein EOF-Zeichen gefunden
        sMsg = "Im Eingabestrom wurde ein EOF-Zeichen gefunden"
   End Select
   
   If Len(sErr) > 0 Then
        mCommEventText = "Fehler: " & sErr
    Else
        mCommEventText = "Meldung: " & sMsg
   End If
End Function

Private Sub mWAIT(s As Single, Optional bWithDoEvents = False)
Dim t As Single
    t = Timer
    If t + s > 86400 Then t = t - 86400
    Do While t + s > Timer
        If bWithDoEvents Then DoEvents
    Loop
End Sub

 

Einkaufsallianz Nord GmbH
In der Mark 2 – 33378 Rheda Wiedenbrück
Tel.: +49 (0)5242 961-398
Joachim.schipper@toennies.de
www.toennies.de

