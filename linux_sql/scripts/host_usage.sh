#! /bin/sh

# Initializes argument variables
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Check number of arguments
if [ "$#" -ne 5 ]; then
    echo "Incorrect number of arguments provided!"
    exit 1
fi

vmstat_mb=$(vmstat --unit M)

#Collect and parses host usage data
hostname=$(hostname | tr -d '\n')
memory_free=$(echo "$vmstat_mb" | tail -n1 | awk '{print $4}'| xargs)
cpu_idle=$(echo "$vmstat_mb" | tail -n1 | awk '{print $15}' | xargs)
cpu_kernel=$(echo "$vmstat_mb" | tail -n1 | awk '{print $14}' | xargs)
disk_io=$(vmstat --unit M -d  | tail -n1 | awk '{print $10}' | xargs)
disk_available=$(df -BM / | tail -n1 | awk '{print $4}' | grep -o '[0-9]*')

timestamp=$(date -u +'%Y-%m-%d %H:%M:%S')

#Declare password env var for psql container
export PGPASSWORD=$psql_password
echo $memory_free
echo $cpu_idle
echo $cpu_kernel
echo $disk_io
echo $disk_available

# Subquery to find matching id in host_info table
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')";
#
##Construct command and execute it
insert_stmt="INSERT INTO host_usage(timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
VALUES('$timestamp', $host_id, '$memory_free', '$cpu_idle', '$cpu_kernel', '$disk_io', '$disk_available')"

psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"

exit $?