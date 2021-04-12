package data.workwithrequest;

import collectionofflats.MyTreeMap;
import commands.Execute;
import data.netdata.Report;
import data.netdata.ReportState;
import data.netdata.Request;

import java.util.Scanner;

public abstract class ExecuteRequest {
    private static Scanner scannerOfCommands;
    public static StringBuilder answer = new StringBuilder();

    public static Report doingRequest(Request request, MyTreeMap myMap) {
        System.out.println("Entering the command: " + request.getCommandName());

        StringBuilder fullRequest = new StringBuilder(request.getCommandName() + request.getArgument());
        scannerOfCommands = new Scanner(fullRequest.toString());

        ReportState stateAnswer = ReportState.OK;
        answer = new StringBuilder();
        try {
            Execute.execute(true, myMap, scannerOfCommands);
            stateAnswer = ReportState.OK;
        } catch (Exception e) {
            stateAnswer = ReportState.ERROR;
            answer.append(e.getMessage());
        }


        return makeReport(stateAnswer, answer);
    }

    public static Report makeReport(ReportState state, StringBuilder body) {
        Report reportToClient = new Report(state, body.toString());

        return reportToClient;
    }
}
