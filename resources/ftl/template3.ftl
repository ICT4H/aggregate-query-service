{
"dataSet": "${dataset}",
"period": "${period}",
"orgUnit": "${organization}",
"dataValues": [
<#list Query_1 as row>
    {
    "dataElement": "AiPqHCbJQJ1",
    "categoryOptionCombo": "u2QXNMacZLt",
    "value": "${row.v1}"
    },
    {
    "dataElement": "AiPqHCbJQJ1",
    "categoryOptionCombo": "DA2N93v7s0O",
    "value": "${row.v2}"
    }
</#list>
]
}