import { createBpmn } from "./functions/BPMN_create.js";
import { createHtmlResource } from "./functions/RESOURCES_create.js";

export function generateRandomBpmn() {
    const bpmn_part1 = '<?xml version="1.0" encoding="UTF-8"?><bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:modeler="http://camunda.org/schema/modeler/1.0" id="Definitions_0hfksvi" targetNamespace="http://bpmn.io/schema/bpmn" exporter="Camunda Modeler" exporterVersion="5.16.0" modeler:executionPlatform="Camunda Platform" modeler:executionPlatformVersion="7.20.0"><bpmn:process id="';
    const bpmn_part2 = '" isExecutable="true" camunda:historyTimeToLive="180"><bpmn:startEvent id="StartEvent_1">  <bpmn:outgoing>Flow_0rtxipq</bpmn:outgoing></bpmn:startEvent><bpmn:task id="Activity_0hsnyv1" name="Esempio 2">  <bpmn:incoming>Flow_0rtxipq</bpmn:incoming>  <bpmn:outgoing>Flow_0d1f1hn</bpmn:outgoing></bpmn:task><bpmn:sequenceFlow id="Flow_0rtxipq" sourceRef="StartEvent_1" targetRef="Activity_0hsnyv1" /><bpmn:endEvent id="Event_0gcfha5">  <bpmn:incoming>Flow_0d1f1hn</bpmn:incoming></bpmn:endEvent><bpmn:sequenceFlow id="Flow_0d1f1hn" sourceRef="Activity_0hsnyv1" targetRef="Event_0gcfha5" /></bpmn:process><bpmndi:BPMNDiagram id="BPMNDiagram_1"><bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="demo_0611_1">  <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">    <dc:Bounds x="179" y="99" width="36" height="36" />  </bpmndi:BPMNShape>  <bpmndi:BPMNShape id="Activity_0hsnyv1_di" bpmnElement="Activity_0hsnyv1">    <dc:Bounds x="270" y="77" width="100" height="80" />    <bpmndi:BPMNLabel />  </bpmndi:BPMNShape>  <bpmndi:BPMNShape id="Event_0gcfha5_di" bpmnElement="Event_0gcfha5">    <dc:Bounds x="432" y="99" width="36" height="36" />  </bpmndi:BPMNShape>  <bpmndi:BPMNEdge id="Flow_0rtxipq_di" bpmnElement="Flow_0rtxipq">    <di:waypoint x="215" y="117" />    <di:waypoint x="270" y="117" />  </bpmndi:BPMNEdge>  <bpmndi:BPMNEdge id="Flow_0d1f1hn_di" bpmnElement="Flow_0d1f1hn">    <di:waypoint x="370" y="117" />    <di:waypoint x="432" y="117" />  </bpmndi:BPMNEdge></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></bpmn:definitions>';
    var id = Math.random().toString(36).slice(2);

    return bpmn_part1 + id + bpmn_part2;
}

export function createBpmnAndGetId(baseUrl, token) {
    const newBpmn = createBpmn(baseUrl, token, generateRandomBpmn());
    return JSON.parse(newBpmn);
}

export function generateAssociationBody(bpmnId) {
    return '{"defaultTemplateId": " ' + bpmnId + '","defaultTemplateVersion": "1","branchesConfigs": [{"branchId": "1","branchDefaultTemplateId": "' + bpmnId + '","branchDefaultTemplateVersion": "1","terminals": [{"templateId": "' + bpmnId + '","templateVersion": "1","terminalIds":["1"]}]}]}';
}

export function generateUpgradedBpmnByDefKey(defKey) {
    const bpmn_part1 = '<?xml version="1.0" encoding="UTF-8"?><bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:camunda="http://camunda.org/schema/1.0/bpmn" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:modeler="http://camunda.org/schema/modeler/1.0" id="Definitions_0hfksvi" targetNamespace="http://bpmn.io/schema/bpmn" exporter="Camunda Modeler" exporterVersion="5.16.0" modeler:executionPlatform="Camunda Platform" modeler:executionPlatformVersion="7.20.0"><bpmn:process id="';
    const bpmn_part2 = '" isExecutable="true" camunda:historyTimeToLive="181"><bpmn:startEvent id="StartEvent_1">  <bpmn:outgoing>Flow_0rtxipq</bpmn:outgoing></bpmn:startEvent><bpmn:task id="Activity_0hsnyv1" name="Esempio 2">  <bpmn:incoming>Flow_0rtxipq</bpmn:incoming>  <bpmn:outgoing>Flow_0d1f1hn</bpmn:outgoing></bpmn:task><bpmn:sequenceFlow id="Flow_0rtxipq" sourceRef="StartEvent_1" targetRef="Activity_0hsnyv1" /><bpmn:endEvent id="Event_0gcfha5">  <bpmn:incoming>Flow_0d1f1hn</bpmn:incoming></bpmn:endEvent><bpmn:sequenceFlow id="Flow_0d1f1hn" sourceRef="Activity_0hsnyv1" targetRef="Event_0gcfha5" /></bpmn:process><bpmndi:BPMNDiagram id="BPMNDiagram_1"><bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="demo_0611_1">  <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">    <dc:Bounds x="179" y="99" width="36" height="36" />  </bpmndi:BPMNShape>  <bpmndi:BPMNShape id="Activity_0hsnyv1_di" bpmnElement="Activity_0hsnyv1">    <dc:Bounds x="270" y="77" width="100" height="80" />    <bpmndi:BPMNLabel />  </bpmndi:BPMNShape>  <bpmndi:BPMNShape id="Event_0gcfha5_di" bpmnElement="Event_0gcfha5">    <dc:Bounds x="432" y="99" width="36" height="36" />  </bpmndi:BPMNShape>  <bpmndi:BPMNEdge id="Flow_0rtxipq_di" bpmnElement="Flow_0rtxipq">    <di:waypoint x="215" y="117" />    <di:waypoint x="270" y="117" />  </bpmndi:BPMNEdge>  <bpmndi:BPMNEdge id="Flow_0d1f1hn_di" bpmnElement="Flow_0d1f1hn">    <di:waypoint x="370" y="117" />    <di:waypoint x="432" y="117" />  </bpmndi:BPMNEdge></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></bpmn:definitions>';
    return bpmn_part1 + defKey + bpmn_part2;
}

export function generateRandomHTML() {
    return (
        `<!DOCTYPE html><html><body><h1>${Math.random().toString(36).slice(2)}</h1></body> </html>`
    )
}

export function createHTMLResource(baseUrl, token) {
    const htmlResource = createHtmlResource(baseUrl, token, generateRandomHTML());
    return JSON.parse(htmlResource);
}