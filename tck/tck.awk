BEGIN { print "<testsuite name=\"org.jainslee.tck\" time=\"1.0\">" }

{
    gsub(/\//, ".", $1);
    #find last dot pos 
    lastDot = match($1, /[^.]+$/);
    #remove .xml
    className = substr($1,1,lastDot - 2);
    #get last dot pos again
    lastDot = match(className, /[^.]+$/);
    #finally get test name
    testCaseName = substr(className,lastDot);
    className = substr(className,1, lastDot - 2);


    printf "<testcase classname=\"";
    print className;
    printf"\" name=\"";
    printf testCaseName;
    print "\" time=\"0.0\">";
    if ($2 =="Failed.") {
       printf "<failure message=\"";
       for(i=3;i<=7;i++){printf "%s ", $i}; 
       print "\">";
       print "<![CDATA["
       for(i=7;i<=NF;i++){printf "%s ", $i};
       print "]]>"
       print "</failure>"
    }
    if ($2 =="Error.") {
       printf "<error message=\"";
       printf $3 
       print "\">";
       print "<![CDATA["
       for(i=4;i<=NF;i++){printf "%s ", $i};
       print "]]>"
       print "</error>"
    }


    print "</testcase>";
}

END { # this assumes all non-blank lines will have an item
    print "</testsuite>";
}

