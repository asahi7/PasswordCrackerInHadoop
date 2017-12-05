STUDENT_ID=${1}
mkdir ${STUDENT_ID}
rsync -rv --exclude=${STUDENT_ID} ./* ./${STUDENT_ID}
tar -zcvf ${STUDENT_ID}.tar.gz ${STUDENT_ID}
